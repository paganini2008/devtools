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
 * NioServer
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NioServer implements Runnable, Server {

	private final AtomicBoolean running = new AtomicBoolean(false);
	private final Reactor reactor;

	public NioServer() {
		this(8);
	}

	public NioServer(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), Executors.newFixedThreadPool(nThreads));
	}

	public NioServer(Executor ioThreads, Executor channelThreads) {
		reactor = new Reactor(ioThreads, channelThreads);
	}

	private ServerSocketChannel serverSocket;
	private int backlog = 128;
	private Transformer transformer = new SerializationTransformer();
	private SocketAddress localAddress = new InetSocketAddress(8090);
	private int readerBufferSize = 2 * 1024;
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

	public void addHandler(ChannelHandler channelHandler) {
		this.reactor.subscribeChannelEvent(channelHandler);
	}

	public void start() throws IOException {

		serverSocket = ServerSocketChannel.open();
		final ServerSocket socket = serverSocket.socket();
		socket.setReceiveBufferSize(readerBufferSize);
		socket.setReuseAddress(true);
		socket.bind(localAddress, backlog);
		serverSocket.configureBlocking(false);
		reactor.registerIoEvent(serverSocket, new AcceptableEventHandler());

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
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * AcceptableEventHandler
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class AcceptableEventHandler implements IoEventHandler {

		AcceptableEventHandler() {
		}

		@Override
		public void onEventFired(IoEvent event) {
			if (event.getCause() != null) {
				return;
			}
			System.out.println("触发：AcceptableEventHandler ");
			final Reactor reactor = (Reactor) event.getSource();
			Channel channel = null;
			try {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) event.getSelectionKey().channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);

				channel = new NioChannel(reactor, socketChannel, transformer);
				reactor.registerIoEvent(socketChannel, new ReadableEventHandler(channel));

				reactor.publishChannelEvent(channel, ChannelEvent.EventType.ACTIVE);
			} catch (IOException e) {
				reactor.publishChannelEvent(channel, ChannelEvent.EventType.FATAL, null, e);
			}
		}

		@Override
		public EventType getEventType() {
			return EventType.ACCEPTABLE;
		}

	}

	/**
	 * 
	 * ReadableEventHandler
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class ReadableEventHandler implements IoEventHandler {

		private final Channel channel;

		ReadableEventHandler(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void onEventFired(IoEvent event) {
			if (event.getCause() != null) {
				return;
			}
			System.out.println("触发：ReadableEventHandler ");
			try {
				channel.read();
			} catch (IOException e) {
				reactor.publishChannelEvent(channel, ChannelEvent.EventType.FATAL, null, e);
			}
		}

		@Override
		public EventType getEventType() {
			return EventType.READABLE;
		}

	}

}
