package com.github.paganini2008.transport.logging;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.github.paganini2008.devtools.ExceptionUtils;
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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * Log4jTransportClientAppender
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class Log4jTransportClientAppender extends AppenderSkeleton {

	private static final String CLUSTER_NAMESPACE = "spring:application:cluster:";
	private static final String APPLICATION_KEY_PREFIX = "socketbird:application:";

	private NioClient nioClient;
	private String redisHost = "localhost";
	private int redisPort = 6379;
	private String password = "";
	private String clusterName;
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

	public void setPartitionerClassName(String partitionerClassName) {
		if (StringUtils.isNotBlank(partitionerClassName)) {
			this.partitioner = BeanUtils.instantiate(partitionerClassName);
		}
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public void setGroupingFieldName(String groupingFieldName) {
		if (partitioner instanceof HashPartitioner && StringUtils.isNotBlank(groupingFieldName)) {
			((HashPartitioner) partitioner).addFieldNames(groupingFieldName.trim().split(","));
		}
	}

	@Override
	public void close() {
		if (jedisPool != null) {
			jedisPool.close();
		}

		if (nioClient != null) {
			nioClient.close();
		}
		this.closed = true;
	}

	@Override
	public void activateOptions() {
		nioClient = new NettyClient();
		nioClient.open();

		jedisPool = getPool();
		doConnect();

		ThreadUtils.scheduleAtFixedRate(() -> {
			doConnect();
			return nioClient.isOpened();
		}, 1, TimeUnit.MINUTES);
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent eventObject) {
		Tuple tuple = Tuple.newTuple();
		tuple.setField("loggerName", eventObject.getLoggerName());
		tuple.setField("message", eventObject.getRenderedMessage());
		tuple.setField("level", eventObject.getLevel().toString());
		tuple.setField("error", ExceptionUtils.toString(eventObject.getThrowableInformation().getThrowable()));
		tuple.setField("mdc", eventObject.getProperties());
		tuple.setField("timestamp", eventObject.getTimeStamp());
		nioClient.send(tuple, partitioner);
	}

	private void doConnect() {
		if (StringUtils.isBlank(clusterName)) {
			throw new TransportClientException("ClusterName must be required.");
		}
		getAddresses().forEach(address -> {
			String[] args = address.split(":", 2);
			try {
				nioClient.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])), location -> {
					LogLog.debug("Logging to: " + location);
				});
			} catch (Exception e) {
				LogLog.error(e.getMessage(), e);
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
			LogLog.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return addresses;
	}

	private JedisPool getPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(1);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxWaitMillis(-1);
		jedisPoolConfig.setTestWhileIdle(true);
		return new JedisPool(jedisPoolConfig, redisHost, redisPort, 60 * 1000, password, 0);
	}

}
