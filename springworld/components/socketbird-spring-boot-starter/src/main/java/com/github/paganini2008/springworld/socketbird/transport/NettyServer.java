package com.github.paganini2008.springworld.socketbird.transport;

import static com.github.paganini2008.springworld.socketbird.Constants.APPLICATION_KEY;
import static com.github.paganini2008.springworld.socketbird.Constants.PORT_RANGE_END;
import static com.github.paganini2008.springworld.socketbird.Constants.PORT_RANGE_START;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.net.NetUtils;
import com.github.paganini2008.springworld.cluster.ClusterId;
import com.github.paganini2008.transport.netty.IdlePolicy;
import com.github.paganini2008.transport.netty.NettySerializationCodecFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
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
	private NettySerializationCodecFactory codecFactory;

	@Value("${socketbird.transport.nioserver.threads:-1}")
	private int threadCount;

	@Value("${socketbird.transport.nioserver.hostName:}")
	private String hostName;

	@Value("${socketbird.transport.nioserver.readerIdleTime:300}")
	private int readerIdleTime;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ClusterId clusterId;

	@Autowired
	private StringRedisTemplate redisTemplate;

	public int start() {
		if (isStarted()) {
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
				pipeline.addLast(new IdleStateHandler(readerIdleTime, 0, 0, TimeUnit.SECONDS));
				pipeline.addLast(codecFactory.getEncoder(), codecFactory.getDecoder());
				pipeline.addLast(readerIdleTime > 0 ? IdlePolicy.CLOSE_BY_SERVER : IdlePolicy.NOOP);
				pipeline.addLast(serverHandler);
			}
		});
		int port = NetUtils.getRandomPort(PORT_RANGE_START, PORT_RANGE_END);
		try {
			SocketAddress socketAddress = StringUtils.isNotBlank(hostName) ? new InetSocketAddress(hostName, port)
					: new InetSocketAddress(port);
			bootstrap.bind(socketAddress).sync();
			String location = (StringUtils.isNotBlank(hostName) ? hostName : NetUtils.getLocalHost()) + ":" + port;
			String key = String.format(APPLICATION_KEY, applicationName);
			redisTemplate.opsForHash().put(key, clusterId.get(), location);
			started.set(true);
			log.info("NettyServer is started on: " + socketAddress);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		return port;
	}

	public void stop() {
		if (!isStarted()) {
			return;
		}
		try {
			if (workerGroup != null) {
				workerGroup.shutdownGracefully();
			}
			if (bossGroup != null) {
				bossGroup.shutdownGracefully();
			}
			started.set(false);
			log.info("NettyServer is stoped successfully.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean isStarted() {
		return started.get();
	}

}
