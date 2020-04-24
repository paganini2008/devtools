package com.github.paganini2008.devtools.nio;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.nio.IoEvent.EventType;

/**
 * 
 * EmbedNioServer
 *
 * @author Fred Feng
 * @since 1.0
 */
public class EmbedNioServer implements Runnable, EmbedServer {

	private final AtomicBoolean running = new AtomicBoolean(false);
	private final Reactor reactor;

	public EmbedNioServer() {
		this(8);
	}

	public EmbedNioServer(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), Executors.newFixedThreadPool(nThreads));
	}

	public EmbedNioServer(Executor ioThreads, Executor channelThreads) {
		reactor = new Reactor(ioThreads, channelThreads);
	}

	private ServerSocketChannel serverSocket;
	private int backlog = 128;
	private Transformer transformer = new SerializationTransformer();
	private SocketAddress localAddress = new InetSocketAddress(8090);
	private int readerBufferSize = 2 * 1024;
	private int autoFlushInterval = 3;
	private Thread runner;

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public SocketAddress getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(SocketAddress localAddress) {
		this.localAddress = localAddress;
	}

	public int getReaderBufferSize() {
		return readerBufferSize;
	}

	public void setReaderBufferSize(int readerBufferSize) {
		this.readerBufferSize = readerBufferSize;
	}

	public int getAutoFlushInterval() {
		return autoFlushInterval;
	}

	public void setAutoFlushInterval(int autoFlushInterval) {
		this.autoFlushInterval = autoFlushInterval;
	}

	public void addHandler(ChannelHandler channelHandler) {
		this.reactor.getChannelEventPublisher().subscribeChannelEvent(channelHandler);
	}

	public void start() throws IOException {

		serverSocket = ServerSocketChannel.open();
		final ServerSocket socket = serverSocket.socket();
		socket.setReceiveBufferSize(readerBufferSize);
		socket.setReuseAddress(true);
		socket.bind(localAddress, backlog);
		serverSocket.configureBlocking(false);
		reactor.getIoEventPublisher().subscribeIoEvent(serverSocket, new AcceptableEventListener());

		running.set(true);
		runner = ThreadUtils.runAsThread(this);
		System.out.println("Start at " + localAddress);
	}

	public void stop() throws IOException {
		running.set(false);
		reactor.wakeup();
		try {
			runner.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (serverSocket != null) {
			serverSocket.close();
		}
		reactor.close();
		System.out.println("Over.");
	}

	public boolean isRunning() {
		return running.get();
	}

	public void run() {
		try {
			while (isRunning()) {
				reactor.select(0L);
			}
		} catch (IOException e) {
			running.set(false);
			throw new IOError(e);
		}
	}

	/**
	 * 
	 * AcceptableEventListener
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class AcceptableEventListener implements IoEventListener {

		@Override
		public void onEventFired(IoEvent event) {
			final Reactor reactor = (Reactor) event.getSource();
			Channel channel = null;
			try {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) event.getSelectionKey().channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);

				channel = new NioChannel(reactor, socketChannel, transformer, autoFlushInterval);
				reactor.getIoEventPublisher().subscribeIoEvent(socketChannel, new ReadableEventListener(channel));

				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, ChannelEvent.EventType.ACTIVE));
			} catch (IOException e) {
				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, ChannelEvent.EventType.FATAL, null, e));
			}
		}

		@Override
		public EventType getEventType() {
			return EventType.ACCEPTABLE;
		}

	}

	/**
	 * 
	 * ReadableEventListener
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class ReadableEventListener implements IoEventListener {

		private final Channel channel;

		ReadableEventListener(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void onEventFired(IoEvent event) {
			try {
				channel.read();
			} catch (IOException e) {
				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(channel, ChannelEvent.EventType.FATAL, null, e));
			}
		}

		@Override
		public EventType getEventType() {
			return EventType.READABLE;
		}

	}

}
