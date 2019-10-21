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
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class NettyClient implements NioClient {

	private EventLoopGroup workerGroup;
	private Bootstrap bootstrap;

	@Value("${socketbird.nioclient.threads:-1}")
	private int threadCount;

	@Value("${socketbird.store.collection.name}")
	private String collection;

	@Autowired
	private NettyClientHandler clientHandler;

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
				pipeline.addLast("handler", clientHandler);
			}
		});
	}

	public void connect(SocketAddress address) {
		if (isConnected(address)) {
			return;
		}
		try {
			bootstrap.connect(address).addListener(new GenericFutureListener<ChannelFuture>() {
				public void operationComplete(ChannelFuture future) throws Exception {
					log.info("NettyClient connect to: " + address);
				}
			}).sync();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void send(Tuple tuple) {
		ChannelContext channelContext = clientHandler.getChannelContext();
		for (ChannelWrapper channel : channelContext.getChannels()) {
			channel.send(tuple);
		}
	}

	public void send(Tuple tuple, Partitioner partitioner) {
		ChannelContext channelContext = clientHandler.getChannelContext();
		ChannelWrapper channel = channelContext.selectChannel(tuple, partitioner);
		if (channel != null) {
			channel.send(tuple);
		}
	}

	public boolean isConnected(SocketAddress address) {
		ChannelContext channelContext = clientHandler.getChannelContext();
		ChannelWrapper channel = channelContext.getChannel(address);
		return channel != null && channel.isActive();
	}

	public void disconnect() {
		ChannelContext channelContext = clientHandler.getChannelContext();
		for (ChannelWrapper channel : channelContext.getChannels()) {
			channel.disconnect();
		}
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
