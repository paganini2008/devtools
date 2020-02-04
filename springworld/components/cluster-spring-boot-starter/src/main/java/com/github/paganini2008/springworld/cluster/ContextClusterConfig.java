package com.github.paganini2008.springworld.cluster;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import lombok.Setter;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * ContextClusterConfig
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
@Setter
@Order(Ordered.LOWEST_PRECEDENCE - 100)
@Configuration
@ConditionalOnProperty(value = "spring.application.cluster.enabled", havingValue = "true", matchIfMissing = true)
@ConfigurationProperties(prefix = "spring.redis")
public class ContextClusterConfig {

	private String host;
	private String password;
	private int port;
	private int dbIndex;

	@Bean
	public ContextClusterConfigProperties configProperties() {
		return new ContextClusterConfigProperties();
	}

	@Bean
	@ConditionalOnMissingBean(ClusterIdGenerator.class)
	public ClusterIdGenerator clusterIdGenerator() {
		return new DefaultClusterIdGenerator();
	}

	@Bean
	public ClusterId clusterId() {
		return new ClusterId();
	}

	@Bean
	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setDatabase(dbIndex);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		jedisClientConfiguration.connectTimeout(Duration.ofMillis(60000)).readTimeout(Duration.ofMillis(60000)).usePooling()
				.poolConfig(jedisPoolConfig());
		JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
		return factory;
	}

	@Bean
	@ConditionalOnMissingBean(JedisPoolConfig.class)
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMinIdle(1);
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxWaitMillis(-1);
		jedisPoolConfig.setTestWhileIdle(true);
		return jedisPoolConfig;
	}

	@Bean(name = "clusterHeartbeatThread", destroyMethod = "stop")
	public ContextClusterHeartbeatThread heartbeatThread() {
		return new ContextClusterHeartbeatThread();
	}

	@Bean
	public ContextClusterAware contextClusterAware() {
		return new ContextClusterAware();
	}

	@Bean
	public ContextMasterBreakdownListener masterBreakdownListener() {
		return new ContextMasterBreakdownListener();
	}
}
