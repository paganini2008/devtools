package com.github.paganini2008.devtools.nio.examples;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.github.paganini2008.devtools.multithreads.AtomicUnsignedInteger;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.nio.ChannelEvent;
import com.github.paganini2008.devtools.nio.ChannelEventPublisher;
import com.github.paganini2008.devtools.nio.ChannelHandler;
import com.github.paganini2008.devtools.nio.DefaultChannelEventPublisher;
import com.github.paganini2008.devtools.nio.Channel;
import com.github.paganini2008.devtools.nio.SerializationTransformer;
import com.github.paganini2008.devtools.nio.Transformer;

/**
 * 
 * NioAcceptor
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NioAcceptor extends NioReactor implements IoAcceptor {

	private ServerSocketChannel serverChannel;
	private int backlog = 128;
	private Transformer transformer = new SerializationTransformer();
	private SocketAddress localAddress = new InetSocketAddress(8090);
	private int readerBufferSize = 2 * 1024;
	private int autoFlushInterval = 3;
	private final AtomicUnsignedInteger index;
	private final ChannelEventPublisher channelEventPublisher;
	private final ConcurrentMap<Integer, NioReader> readers = new ConcurrentHashMap<Integer, NioReader>();

	public NioAcceptor() {
		this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
	}

	public NioAcceptor(Executor executor) {
		this(executor, executor);
	}

	public NioAcceptor(Executor bossExecutor, Executor workerExecutor) {
		super(bossExecutor);
		this.index = new AtomicUnsignedInteger(1, Runtime.getRuntime().availableProcessors());
		this.channelEventPublisher = new DefaultChannelEventPublisher(workerExecutor);
	}

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
		this.channelEventPublisher.subscribeChannelEvent(channelHandler);
	}

	public void start() throws IOException {
		serverChannel = ServerSocketChannel.open();
		final ServerSocket socket = serverChannel.socket();
		if (readerBufferSize > 0) {
			socket.setReceiveBufferSize(readerBufferSize);
		}
		socket.setReuseAddress(true);
		socket.bind(localAddress, backlog);
		serverChannel.configureBlocking(false);
		register(serverChannel, SelectionKey.OP_ACCEPT, null);
		logger.info("Server bind at " + localAddress);
	}

	public void stop() {
		for (NioReactor reactor : readers.values()) {
			reactor.destroy();
		}
		channelEventPublisher.destroy();
		if (serverChannel != null) {
			try {
				serverChannel.close();
			} catch (IOException ignored) {
			}
		}
		destroy();
		ExecutorUtils.gracefulShutdown(executor, 60000L);
		logger.info("Server has stopped.");
	}

	@Override
	protected boolean isSelectable(SelectionKey selectionKey) {
		return selectionKey.isAcceptable();
	}

	@Override
	protected void process(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
		socketChannel.configureBlocking(false);
		Channel channel = new NioChannel(channelEventPublisher, socketChannel, transformer, autoFlushInterval);
		NioReader nextReactor = readers.getOrDefault(index.getAndIncrement(), new NioReader(executor));
		nextReactor.register(socketChannel, SelectionKey.OP_READ, channel);
		channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, ChannelEvent.EventType.ACTIVE));
	}

}
