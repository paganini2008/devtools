package com.github.paganini2008.springworld.socketbird.transport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.net.NetUtils;
import com.github.paganini2008.springworld.cluster.ClusterId;
import com.github.paganini2008.springworld.socketbird.Constants;
import com.github.paganini2008.springworld.socketbird.Serializer;
import com.github.paganini2008.springworld.socketbird.transport.NettyTransport.ByteToTupleDecorder;
import com.github.paganini2008.springworld.socketbird.transport.NettyTransport.TupleToByteEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * NettyServer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class NettyServer implements NioServer {

	private final AtomicBoolean started = new AtomicBoolean(false);

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@Autowired
	private NettyServerHandler serverHandler;

	@Autowired
	private Serializer serializer;

	@Value("${socketbird.nioserver.threads:-1}")
	private int threadCount;

	@Value("${socketbird.nioserver.hostName:}")
	private String hostName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ClusterId instanceId;

	@Autowired
	private StringRedisTemplate redisTemplate;

	public int start() {
		if (started.get()) {
			throw new IllegalStateException("NettyServer has been started.");
		}
		int nThreads = threadCount > 0 ? threadCount : Runtime.getRuntime().availableProcessors() * 2;
		bossGroup = new NioEventLoopGroup(nThreads);
		workerGroup = new NioEventLoopGroup(nThreads);
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.childOption(ChannelOption.SO_RCVBUF, 2 * 1024 * 1024);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new TupleToByteEncoder(serializer), new ByteToTupleDecorder(serializer));
				pipeline.addLast(serverHandler);
			}
		});
		int port = NetUtils.getRandomPort(50000, 60000);
		try {
			SocketAddress socketAddress = StringUtils.isNotBlank(hostName) ? new InetSocketAddress(hostName, port)
					: new InetSocketAddress(port);
			bootstrap.bind(socketAddress).sync();
			String location = (StringUtils.isNotBlank(hostName) ? hostName : NetUtils.getLocalHost()) + ":" + port;
			String key = String.format(Constants.APPLICATION_KEY, applicationName);
			redisTemplate.opsForHash().put(key, instanceId.get(), location);
			started.set(true);
			log.info("NettyServer is started on: " + socketAddress);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		return port;
	}

	public void stop() {
		try {
			if (workerGroup != null) {
				workerGroup.shutdownGracefully();
			}
			if (bossGroup != null) {
				bossGroup.shutdownGracefully();
			}
			started.set(false);
			log.info("NettyServer stop successfully.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean isStarted() {
		return started.get();
	}

}
