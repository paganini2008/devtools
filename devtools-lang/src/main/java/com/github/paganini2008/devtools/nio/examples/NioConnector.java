package com.github.paganini2008.devtools.nio.examples;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.nio.Channel;
import com.github.paganini2008.devtools.nio.ChannelEvent;
import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;
import com.github.paganini2008.devtools.nio.ChannelEventPublisher;
import com.github.paganini2008.devtools.nio.ChannelHandler;
import com.github.paganini2008.devtools.nio.DefaultChannelEventPublisher;
import com.github.paganini2008.devtools.nio.SerializationTransformer;
import com.github.paganini2008.devtools.nio.Transformer;

/**
 * 
 * NioConnector
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NioConnector extends NioReactor implements IoConnector {

	public NioConnector() {
		this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
	}

	public NioConnector(Executor executor) {
		this(executor, executor);
	}

	public NioConnector(Executor bossExecutor, Executor workerExecutor) {
		super(bossExecutor);
		this.reader = new NioReader(bossExecutor);
		this.channelEventPublisher = new DefaultChannelEventPublisher(workerExecutor);
	}

	private final NioReader reader;
	private final ChannelEventPublisher channelEventPublisher;
	private Channel channel;
	private Transformer transformer = new SerializationTransformer();
	private int writerBatchSize = 0;
	private int writerBufferSize = 1024;
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

	public void setAutoFlushInterval(int autoFlushInterval) {
		this.autoFlushInterval = autoFlushInterval;
	}

	public void addHandler(ChannelHandler channelHandler) {
		this.channelEventPublisher.subscribeChannelEvent(channelHandler);
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
		channel = new NioChannel(channelEventPublisher, socketChannel, transformer, autoFlushInterval);
		register(socketChannel, SelectionKey.OP_CONNECT, channel);
	}

	public void write(Object object) {
		try {
			channel.write(object, writerBatchSize);
		} catch (IOException e) {
			channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
		}
	}

	public void flush() {
		try {
			channel.flush();
		} catch (IOException e) {
			channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
		}
	}

	public boolean isActive() {
		return channel != null && channel.isActive();
	}

	public void close() {
		reader.destroy();
		channelEventPublisher.destroy();
		if (channel != null) {
			channel.close();
		}
		destroy();
		ExecutorUtils.gracefulShutdown(executor, 60000L);
	}

	@Override
	protected boolean isSelectable(SelectionKey selectionKey) {
		return selectionKey.isConnectable();
	}

	@Override
	protected void process(SelectionKey selectionKey) throws IOException {
		final SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		boolean connected = true;
		if (socketChannel.isConnectionPending()) {
			try {
				while (!socketChannel.finishConnect()) {
					;
				}
			} catch (IOException e) {
				connected = false;
				channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
			}
		}
		if (connected) {
			reader.register(socketChannel, SelectionKey.OP_READ, channel);
			channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.ACTIVE));
		}
	}

}
