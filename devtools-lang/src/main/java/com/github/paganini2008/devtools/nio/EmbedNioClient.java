package com.github.paganini2008.devtools.nio;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;

/**
 * 
 * EmbedNioClient
 *
 * @author Fred Feng
 * @since 1.0
 */
public class EmbedNioClient implements Runnable, EmbedClient {

	public EmbedNioClient() {
		this(8);
	}

	public EmbedNioClient(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), Executors.newFixedThreadPool(nThreads));
	}

	public EmbedNioClient(Executor ioThreads, Executor channelThreads) {
		reactor = new Reactor(ioThreads, channelThreads);
	}

	private final Reactor reactor;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Channel channel;
	private Transformer transformer = new SerializationTransformer();
	private Thread runner;
	private int writerBatchSize = 10;
	private int writerBufferSize = -1;
	private int bufferPoolSize = 128;
	private int autoFlushInterval = 3;

	public int getWriterBatchSize() {
		return writerBatchSize;
	}

	public void setWriterBatchSize(int writerBatchSize) {
		this.writerBatchSize = writerBatchSize;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public int getWriterBufferSize() {
		return writerBufferSize;
	}

	public void setWriterBufferSize(int writerBufferSize) {
		this.writerBufferSize = writerBufferSize;
	}

	public int getAutoFlushInterval() {
		return autoFlushInterval;
	}

	public int getBufferPoolSize() {
		return bufferPoolSize;
	}

	public void setBufferPoolSize(int bufferPoolSize) {
		this.bufferPoolSize = bufferPoolSize;
	}

	public void setAutoFlushInterval(int autoFlushInterval) {
		this.autoFlushInterval = autoFlushInterval;
	}

	public void addHandler(ChannelHandler channelHandler) {
		this.reactor.getChannelEventPublisher().subscribeChannelEvent(channelHandler);
	}

	public void connect(String hostName, int port) throws IOException {
		connect(new InetSocketAddress(hostName, port));
	}

	public void connect(SocketAddress remoteAddress) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		final Socket socket = socketChannel.socket();
		socket.setKeepAlive(true);
		socket.setReuseAddress(true);
		socket.setTcpNoDelay(true);
		if (writerBufferSize > 0) {
			socket.setSendBufferSize(writerBufferSize);
		}
		socketChannel.configureBlocking(false);
		socketChannel.connect(remoteAddress);

		reactor.getIoEventPublisher().subscribeIoEvent(socketChannel, new ConnectableEventListener());
		channel = new NioChannel(reactor, socketChannel, transformer, bufferPoolSize, autoFlushInterval);

		running.set(true);
		runner = ThreadUtils.runAsThread(this);
	}

	public void write(Object object) {
		try {
			channel.write(object, writerBatchSize);
		} catch (IOException e) {
			reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
		}
	}

	public void flush() {
		try {
			channel.flush();
		} catch (IOException e) {
			reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	public boolean isActive() {
		return channel != null && channel.isActive();
	}

	public void close() throws IOException {
		running.set(false);
		try {
			runner.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (channel != null) {
			channel.close();
		}
		reactor.close();
	}

	/**
	 * 
	 * ConnectableEventListener
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class ConnectableEventListener implements IoEventListener {

		@Override
		public void onEventFired(IoEvent event) {
			final Reactor reactor = (Reactor) event.getSource();
			final SelectionKey selectionKey = event.getSelectionKey();
			final SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			boolean connected = true;
			if (socketChannel.isConnectionPending()) {
				try {
					while (!socketChannel.finishConnect()) {
						;
					}
				} catch (IOException e) {
					connected = false;
					reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, EventType.INACTIVE, null, e));
				}
			}
			if (connected) {
				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, EventType.ACTIVE));
			}
		}

		@Override
		public IoEvent.EventType getEventType() {
			return IoEvent.EventType.CONNECTABLE;
		}

	}

	public void run() {
		try {
			while (isRunning()) {
				reactor.select(1000L);
			}
		} catch (IOException e) {
			running.set(false);
			throw new IOError(e);
		}
	}

}
