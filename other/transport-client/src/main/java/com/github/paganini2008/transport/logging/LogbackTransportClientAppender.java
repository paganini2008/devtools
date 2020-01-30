package com.github.paganini2008.transport.logging;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.transport.HashPartitioner;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.RoundRobinPartitioner;
import com.github.paganini2008.transport.TransportClientException;
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
 * LogbackTransportClientAppender
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class LogbackTransportClientAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private static final String CLUSTER_NAMESPACE = "spring:application:cluster:";
	private static final String APPLICATION_KEY_PREFIX = "socketbird:application:";

	private NioClient nioClient;
	private String redisHost = "localhost";
	private int redisPort = 6379;
	private String password = "";
	private String clusterName;
	private int startupDelay = 0;
	private Partitioner partitioner = new RoundRobinPartitioner();
	private JedisPool jedisPool;

	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setStartupDelay(int startupDelay) {
		this.startupDelay = startupDelay;
	}

	public void setPartitionerClassName(String partitionerClassName) {
		if (StringUtils.isNotBlank(partitionerClassName)) {
			this.partitioner = BeanUtils.instantiate(partitionerClassName);
		}
	}

	public void setGroupingFieldName(String groupingFieldName) {
		if (partitioner instanceof HashPartitioner && StringUtils.isNotBlank(groupingFieldName)) {
			((HashPartitioner) partitioner).addFieldNames(groupingFieldName.trim().split(","));
		}
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		if (nioClient == null || !nioClient.isOpened()) {
			return;
		}

		Tuple tuple = Tuple.newTuple();
		tuple.setField("loggerName", eventObject.getLoggerName());
		tuple.setField("message", eventObject.getFormattedMessage());
		tuple.setField("level", eventObject.getLevel().levelStr);
		tuple.setField("error", ThrowableProxyUtil.asString(eventObject.getThrowableProxy()));
		tuple.setField("mdc", eventObject.getMDCPropertyMap());
		tuple.setField("marker", eventObject.getMarker().getName());
		tuple.setField("timestamp", eventObject.getTimeStamp());
		nioClient.send(tuple, partitioner);
	}

	@Override
	public void start() {
		if (startupDelay > 0) {
			ThreadUtils.schedule(() -> {
				doStart();
			}, startupDelay, TimeUnit.SECONDS);
		} else {
			doStart();
		}
	}

	private void doStart() {
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

	private void doConnect() {
		if (StringUtils.isBlank(clusterName)) {
			throw new TransportClientException("ClusterName must be required.");
		}
		getAddresses().forEach(address -> {
			String[] args = address.split(":", 2);
			try {
				nioClient.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])), location -> {
					addInfo("Logging to: " + location);
				});
			} catch (Exception e) {
				addError(e.getMessage(), e);
			}
		});
	}

	private List<String> getAddresses() {
		List<String> addresses = new ArrayList<String>();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String fullName = CLUSTER_NAMESPACE + clusterName;
			List<String> clusterIds = jedis.lrange(fullName, 0, -1);
			if (CollectionUtils.isNotEmpty(clusterIds)) {
				String key = APPLICATION_KEY_PREFIX + clusterName;
				addresses.addAll(jedis.hmget(key, clusterIds.toArray(new String[0])));
			}
		} catch (Exception e) {
			addError(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return addresses;
	}

	private JedisPool getPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(2);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxWaitMillis(-1);
		jedisPoolConfig.setTestWhileIdle(true);
		return new JedisPool(jedisPoolConfig, redisHost, redisPort, 60 * 1000, password, 0);
	}

}
