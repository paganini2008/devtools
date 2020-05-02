package com.github.paganini2008.devtools.nio.test;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.nio.Channel;
import com.github.paganini2008.devtools.nio.ChannelEvent;
import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;
import com.github.paganini2008.devtools.nio.ChannelEventPublisher;
import com.github.paganini2008.devtools.nio.ChannelHandler;
import com.github.paganini2008.devtools.nio.DefaultChannelEventPublisher;
import com.github.paganini2008.devtools.nio.SerializationTransformer;
import com.github.paganini2008.devtools.nio.Transformer;
import com.github.paganini2008.devtools.nio.examples.IoConnector;

/**
 * 
 * AioConnector
 *
 * @author Fred Feng
 * @since 1.0
 */
public class AioConnector implements IoConnector {

	private static final Log logger = LogFactory.getLog(AioConnector.class);
	private final ChannelEventPublisher channelEventPublisher;
	private Channel channel;
	private Transformer transformer = new SerializationTransformer();
	private int writerBatchSize = 0;
	private int writerBufferSize = 1024;
	private int autoFlushInterval = 3;

	public AioConnector() {
		this.channelEventPublisher = new DefaultChannelEventPublisher(ExecutorUtils.directExecutor());
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public int getWriterBatchSize() {
		return writerBatchSize;
	}

	public void setWriterBatchSize(int writerBatchSize) {
		this.writerBatchSize = writerBatchSize;
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

	@Override
	public void addHandler(ChannelHandler channelHandler) {
		this.channelEventPublisher.subscribeChannelEvent(channelHandler);
	}

	@Override
	public void connect(SocketAddress remoteAddress) throws IOException {
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
		socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, writerBufferSize);
		socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		try {
			socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		} catch (RuntimeException e) {
			logger.warn(e.getMessage());
		}
		channel = new AioChannel(channelEventPublisher, socketChannel, transformer, autoFlushInterval);
		socketChannel.connect(remoteAddress, channel, new CompletionHandler<Void, Channel>() {

			@Override
			public void completed(Void result, Channel channel) {
				channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.ACTIVE));
			}

			@Override
			public void failed(Throwable e, Channel channel) {
				channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
			}
		});

	}

	@Override
	public void write(Object object) {
		try {
			channel.write(object, writerBatchSize);
		} catch (IOException e) {
			channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
		}
	}

	@Override
	public void flush() {
		try {
			channel.flush();
		} catch (IOException e) {
			channelEventPublisher.publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
		}
	}

	@Override
	public boolean isActive() {
		return channel.isActive();
	}

	@Override
	public void close() {
		channelEventPublisher.destroy();
		if (channel != null) {
			channel.close();
		}
	}

}
