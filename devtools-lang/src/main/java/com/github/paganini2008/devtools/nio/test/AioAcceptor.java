package com.github.paganini2008.devtools.nio.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.nio.Channel;
import com.github.paganini2008.devtools.nio.ChannelEvent;
import com.github.paganini2008.devtools.nio.ChannelEventPublisher;
import com.github.paganini2008.devtools.nio.ChannelHandler;
import com.github.paganini2008.devtools.nio.DefaultChannelEventPublisher;
import com.github.paganini2008.devtools.nio.SerializationTransformer;
import com.github.paganini2008.devtools.nio.Transformer;
import com.github.paganini2008.devtools.nio.examples.IoAcceptor;

/**
 * 
 * AioAcceptor
 *
 * @author Fred Feng
 * @since 1.0
 */
public class AioAcceptor implements IoAcceptor {

	private static final Log logger = LogFactory.getLog(AioAcceptor.class);
	private final ExecutorService bossExecutor;
	private final ChannelEventPublisher channelEventPublisher;
	private AsynchronousChannelGroup channelGroup;
	private AsynchronousServerSocketChannel channel;
	private int backlog = 128;
	private Transformer transformer = new SerializationTransformer();
	private SocketAddress localAddress = new InetSocketAddress(8090);
	private int readerBufferSize = 2 * 1024;
	private int autoFlushInterval = 3;
	private final AtomicBoolean opened = new AtomicBoolean();
	private final Map<AsynchronousSocketChannel, Channel> channelHolder = new ConcurrentHashMap<AsynchronousSocketChannel, Channel>();

	public AioAcceptor() {
		this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
	}

	public AioAcceptor(ExecutorService executor) {
		this(executor, executor);
	}

	public AioAcceptor(ExecutorService bossExecutor, ExecutorService workerExecutor) {
		this.bossExecutor = bossExecutor;
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

	public int getAutoFlushInterval() {
		return autoFlushInterval;
	}

	public void setAutoFlushInterval(int autoFlushInterval) {
		this.autoFlushInterval = autoFlushInterval;
	}

	public void addHandler(ChannelHandler channelHandler) {
		this.channelEventPublisher.subscribeChannelEvent(channelHandler);
	}

	private class AcceptorHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

		@Override
		public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
			channel.accept(null, this);
			Channel channelWrapper = channelHolder.getOrDefault(socketChannel,
					new AioChannel(channelEventPublisher, socketChannel, transformer, autoFlushInterval));
			channelEventPublisher.publishChannelEvent(new ChannelEvent(channelWrapper, ChannelEvent.EventType.ACTIVE));
			channelWrapper.read();

		}

		@Override
		public void failed(Throwable e, Object attachment) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public void start() throws IOException {
		channelGroup = AsynchronousChannelGroup.withThreadPool(bossExecutor);
		channel = AsynchronousServerSocketChannel.open(channelGroup);
		channel.setOption(StandardSocketOptions.SO_RCVBUF, readerBufferSize);
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		channel.bind(localAddress, backlog);
		channel.accept(null, new AcceptorHandler());
		opened.set(true);
		logger.info("Server bind at " + localAddress);
	}

	@Override
	public void stop() {
		channelEventPublisher.destroy();
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException ignored) {
			}
		}
		if (channelGroup != null) {
			try {
				channelGroup.shutdownNow();
			} catch (IOException ignored) {
			}
		}
		ExecutorUtils.gracefulShutdown(bossExecutor, 60000L);
		logger.info("Server has stopped.");
	}

	@Override
	public boolean isOpened() {
		return opened.get();
	}

}
