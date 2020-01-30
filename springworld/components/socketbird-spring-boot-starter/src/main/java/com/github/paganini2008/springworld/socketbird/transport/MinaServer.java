package com.github.paganini2008.springworld.socketbird.transport;

import static com.github.paganini2008.springworld.socketbird.Constants.APPLICATION_KEY;
import static com.github.paganini2008.springworld.socketbird.Constants.PORT_RANGE_END;
import static com.github.paganini2008.springworld.socketbird.Constants.PORT_RANGE_START;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.buffer.CachedBufferAllocator;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.net.NetUtils;
import com.github.paganini2008.springworld.cluster.ClusterId;
import com.github.paganini2008.transport.mina.MinaSerializationCodecFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MinaServer
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
@Slf4j
public class MinaServer implements NioServer {

	static {
		IoBuffer.setUseDirectBuffer(SystemPropertyUtils.getBoolean("transport.nioclient.mina.useDirectBuffer", false));
		IoBuffer.setAllocator(new CachedBufferAllocator());
	}

	private final AtomicBoolean started = new AtomicBoolean(false);
	private NioSocketAcceptor ioAcceptor;
	private SocketAddress localAddress;

	@Autowired
	private MinaServerHandler serverHandler;

	@Autowired
	private MinaSerializationCodecFactory codecFactory;

	@Value("${socketbird.transport.nioserver.threads:-1}")
	private int threadCount;

	@Value("${socketbird.transport.nioserver.hostName:}")
	private String hostName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ClusterId clusterId;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public int start() {
		if (isStarted()) {
			throw new IllegalStateException("MinaServer has been started.");
		}
		final int nThreads = threadCount <= 0 ? Runtime.getRuntime().availableProcessors() * 2 : threadCount;
		ioAcceptor = new NioSocketAcceptor(nThreads);
		ioAcceptor.setBacklog(1024);
		SocketSessionConfig sessionConfig = ioAcceptor.getSessionConfig();
		sessionConfig.setKeepAlive(true);
		sessionConfig.setReuseAddress(true);
		sessionConfig.setReadBufferSize(2 * 1024 * 1024);
		sessionConfig.setReceiveBufferSize(2 * 1024 * 1024);
		sessionConfig.setSoLinger(0);
		ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
		ioAcceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newFixedThreadPool(nThreads)));
		ioAcceptor.setHandler(serverHandler);
		int port = NetUtils.getRandomPort(PORT_RANGE_START, PORT_RANGE_END);
		try {
			localAddress = StringUtils.isNotBlank(hostName) ? new InetSocketAddress(hostName, port) : new InetSocketAddress(port);
			ioAcceptor.bind(localAddress);
			String location = (StringUtils.isNotBlank(hostName) ? hostName : NetUtils.getLocalHost()) + ":" + port;
			String key = String.format(APPLICATION_KEY, applicationName);
			redisTemplate.opsForHash().put(key, clusterId.get(), location);
			started.set(true);
			log.info("MinaServer is started on: " + localAddress);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return port;
	}

	@Override
	public void stop() {
		if (ioAcceptor == null || !isStarted()) {
			return;
		}
		try {
			ioAcceptor.unbind(localAddress);
			ExecutorFilter executorFilter = (ExecutorFilter) ioAcceptor.getFilterChain().get("threadPool");
			if (executorFilter != null) {
				executorFilter.destroy();
			}
			ioAcceptor.getFilterChain().clear();
			ioAcceptor.dispose();
			ioAcceptor = null;

			started.set(false);
			log.info("MinaServer is closed.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean isStarted() {
		return started.get();
	}

}
