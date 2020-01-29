package com.github.paganini2008.transport.netty;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.transport.HandshakeCompletedListener;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.TransportClientException;
import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.serializer.Serializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 
 * NettyClient
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class NettyClient implements NioClient {

	private final NettyChannelContext channelContext = new NettyChannelContext();
	private final AtomicBoolean opened = new AtomicBoolean(false);
	private EventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private NettySerializationCodecFactory codecFactory;

	public void setSerializer(Serializer serializer) {
		this.codecFactory = new NettySerializationCodecFactory(serializer);
	}

	public void open() {
		final int nThreads = SystemPropertyUtils.getInteger("transport.nioclient.threads", Runtime.getRuntime().availableProcessors() * 2);
		workerGroup = new NioEventLoopGroup(nThreads);
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.SO_SNDBUF, 1024 * 1024);
		if (codecFactory == null) {
			codecFactory = new NettySerializationCodecFactory();
		}
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(codecFactory.getEncoder(), codecFactory.getDecoder());
				pipeline.addLast("handler", channelContext);
			}
		});
		opened.set(true);
	}

	public boolean isOpened() {
		return opened.get();
	}

	public void connect(final SocketAddress address, final HandshakeCompletedListener completedListener) {
		if (!isConnected(address)) {
			try {
				bootstrap.connect(address).addListener(new GenericFutureListener<ChannelFuture>() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (completedListener != null) {
							completedListener.operationComplete(address);
						}
					}
				}).sync();
			} catch (InterruptedException e) {
				throw new TransportClientException(e.getMessage(), e);
			}
		}
	}

	public void send(Tuple tuple) {
		channelContext.getChannels().forEach(channel -> {
			doSend(channel, tuple);
		});
	}

	public void send(Tuple tuple, Partitioner partitioner) {
		Channel channel = channelContext.selectChannel(tuple, partitioner);
		if (channel != null) {
			doSend(channel, tuple);
		}
	}

	protected void doSend(Channel channel, Tuple tuple) {
		try {
			channel.writeAndFlush(tuple);
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
	}

	public void close() {
		try {
			channelContext.getChannels().forEach(channel -> {
				channel.close();
			});
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
		try {
			workerGroup.shutdownGracefully();
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
		opened.set(false);
	}

	public boolean isConnected(SocketAddress address) {
		Channel channel = channelContext.getChannel(address);
		return channel != null && channel.isActive();
	}

}
