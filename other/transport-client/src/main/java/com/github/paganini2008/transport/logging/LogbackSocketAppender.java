package com.github.paganini2008.transport.logging;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.RoundRobinPartitioner;
import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.netty.NettyClient;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * LogbackSocketAppender
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class LogbackSocketAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private NioClient nioClient;
	private String host = "localhost";
	private int port = 6379;
	private String password = "";
	private String identifier;
	private Partitioner partitioner = new RoundRobinPartitioner();
	private JedisPool jedisPool;

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPartitioner(Partitioner partitioner) {
		this.partitioner = partitioner;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		Tuple tuple = Tuple.newTuple();
		tuple.setField("message", eventObject.getFormattedMessage());
		tuple.setField("level", eventObject.getLevel().levelStr);
		tuple.setField("cause", ThrowableProxyUtil.asString(eventObject.getThrowableProxy()));
		tuple.setField("timestamp", eventObject.getTimeStamp());
		nioClient.send(tuple, partitioner);
	}

	@Override
	public void start() {
		nioClient = new NettyClient();
		nioClient.open();

		jedisPool = getPool();
		doConnect();

		ThreadUtils.scheduleAtFixedRate(() -> {
			doConnect();
			return nioClient.isOpened();
		}, 1, TimeUnit.MINUTES);

		super.start();
	}

	private void doConnect() {
		getAddresses().forEach(address -> {
			String[] args = address.split(":", 2);
			try {
				nioClient.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
			} catch (Exception e) {
				addError(e.getMessage(), e);
			}
		});
	}

	@Override
	public void stop() {
		super.stop();

		if (jedisPool != null) {
			jedisPool.close();
		}

		if (nioClient != null) {
			nioClient.close();
		}
	}

	private List<String> getAddresses() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmget(identifier);
		} finally {
			jedis.close();
		}
	}

	protected JedisPool getPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMinIdle(1);
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxWaitMillis(-1);
		jedisPoolConfig.setTestWhileIdle(true);
		return new JedisPool(jedisPoolConfig, host, port, 60 * 1000, password, 0);
	}

}
