package com.github.paganini2008.transport.logging;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

import com.github.paganini2008.devtools.ExceptionUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.TransportClientException;
import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.netty.NettyClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * Log4j2TransportClientAppender
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Plugin(name = Log4j2TransportClientAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class Log4j2TransportClientAppender extends AbstractAppender {

	public static final String PLUGIN_NAME = "TransportClient";
	private static final String CLUSTER_NAMESPACE = "spring:application:cluster:";
	private static final String APPLICATION_KEY_PREFIX = "socketbird:application:";

	public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
			implements org.apache.logging.log4j.core.util.Builder<Log4j2TransportClientAppender> {

		@PluginAttribute(value = "redisHost", defaultString = "localhost")
		private String redisHost;

		@PluginAttribute(value = "redisPort", defaultInt = 6379)
		private int redisPort;

		@PluginAttribute(value = "password", defaultString = "")
		private String password;

		@PluginAttribute("clusterName")
		private String clusterName;

		@PluginAttribute(value = "partitionerClassName", defaultString = "com.github.paganini2008.transport.RoundRobinPartitioner")
		private String partitionerClassName;

		@Override
		public Log4j2TransportClientAppender build() {
			final Layout<? extends Serializable> layout = getLayout();
			if (layout == null) {
				LOGGER.error("No layout provided for Log4j2TransportClientAppender");
				return null;
			}
			TransportClient transportClient = new TransportClient();
			transportClient.redisHost = redisHost;
			transportClient.redisPort = redisPort;
			transportClient.password = password;
			transportClient.clusterName = clusterName;
			transportClient.partitioner = BeanUtils.instantiate(partitionerClassName);
			return new Log4j2TransportClientAppender(getName(), getFilter(), layout, isIgnoreExceptions(), getPropertyArray(),
					transportClient);
		}

		public String getRedisHost() {
			return redisHost;
		}

		public int getRedisPort() {
			return redisPort;
		}

		public String getPassword() {
			return password;
		}

		public String getClusterName() {
			return clusterName;
		}

		public String getPartitionerClassName() {
			return partitionerClassName;
		}

		public B setRedisHost(final String host) {
			this.redisHost = host;
			return asBuilder();
		}

		public B setRedisPort(final int port) {
			this.redisPort = port;
			return asBuilder();
		}

		public B setPassword(final String password) {
			this.password = password;
			return asBuilder();
		}

		public B setClusterName(final String clusterName) {
			this.clusterName = clusterName;
			return asBuilder();
		}

		public B setPartitionerClassName(String partitionerClassName) {
			this.partitionerClassName = partitionerClassName;
			return asBuilder();
		}

	}

	@PluginBuilderFactory
	public static <B extends Builder<B>> B newBuilder() {
		return new Builder<B>().asBuilder();
	}

	Log4j2TransportClientAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions,
			Property[] properties, TransportClient transportClient) {
		super(name, filter, layout, ignoreExceptions, properties);
		this.transportClient = transportClient;
	}

	private final TransportClient transportClient;

	@Override
	public void start() {
		super.start();
		transportClient.configure();
	}

	@Override
	public boolean stop(final long timeout, final TimeUnit timeUnit) {
		setStopping();
		boolean stopped = super.stop(timeout, timeUnit, false);
		stopped &= transportClient.close();
		setStopped();
		return stopped;
	}

	@Override
	public void append(LogEvent eventObject) {
		Tuple tuple = Tuple.newTuple();
		tuple.setField("loggerName", eventObject.getLoggerName());
		tuple.setField("message", eventObject.getMessage());
		tuple.setField("level", eventObject.getLevel().name());
		tuple.setField("error", ExceptionUtils.toString(eventObject.getThrown()));
		tuple.setField("mdc", eventObject.getContextData());
		tuple.setField("marker", eventObject.getMarker().getName());
		tuple.setField("timestamp", eventObject.getTimeMillis());
		transportClient.send(tuple);
	}

	/**
	 * 
	 * TransportClient
	 * 
	 * @author Fred Feng
	 * @created 2019-10
	 * @revised 2019-12
	 * @version 1.0
	 */
	static class TransportClient {

		String redisHost;
		int redisPort;
		String password;
		String clusterName;
		Partitioner partitioner;
		NioClient nioClient;
		JedisPool jedisPool;

		public void configure() {

			nioClient = new NettyClient();
			nioClient.open();

			jedisPool = getPool();
			doConnect();

			ThreadUtils.scheduleAtFixedRate(() -> {
				doConnect();
				return nioClient.isOpened();
			}, 1, TimeUnit.MINUTES);
		}

		public void send(Tuple data) {
			nioClient.send(data, partitioner);
		}

		public boolean close() {
			try {
				if (jedisPool != null) {
					jedisPool.close();
				}
				if (nioClient != null) {
					nioClient.close();
				}
				return true;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return false;
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
						LOGGER.info("Logging to: " + location);
					});
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
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
				LOGGER.error(e.getMessage(), e);
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

}
