package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.socketbird.Serializer;
import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.NettyTransport.ByteToTupleDecorder;
import com.github.paganini2008.springworld.socketbird.transport.NettyTransport.TupleToByteEncoder;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;

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
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * NettyClient
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class NettyClient implements NioClient {

	private EventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private Channel channel;

	@Value("${socketbird.nioclient.threads:-1}")
	private int threadCount;

	@Autowired
	private Serializer serializer;

	public void open() {
		final int nThreads = threadCount > 0 ? threadCount : Runtime.getRuntime().availableProcessors() * 2;
		workerGroup = new NioEventLoopGroup(nThreads);
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.SO_SNDBUF, 1024 * 1024);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new TupleToByteEncoder(serializer), new ByteToTupleDecorder(serializer));
			}
		});
	}

	public void connect(SocketAddress address) {
		if (isConnected(address)) {
			return;
		}
		try {
			channel = bootstrap.connect(address).addListener(new GenericFutureListener<ChannelFuture>() {
				public void operationComplete(ChannelFuture future) throws Exception {
					log.info("NettyClient connect to: " + address);
				}
			}).sync().channel();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void send(Tuple tuple) {
		channel.writeAndFlush(tuple);
	}

	public void send(Tuple tuple, Partitioner partitioner) {
		throw new UnsupportedOperationException();
	}

	public boolean isConnected(SocketAddress address) {
		return channel != null && channel.isActive();
	}

	public void disconnect() {
		channel.close();
	}

	public void close() {
		disconnect();
		try {
			if (workerGroup != null) {
				workerGroup.shutdownGracefully();
			}
			log.info("NettyClient is closed.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
