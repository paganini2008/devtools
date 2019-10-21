package com.github.paganini2008.springworld.cluster.implementor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import lombok.Setter;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * ContextClusterConfiguration
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
@Setter
@Order(100)
@Configuration
@ConditionalOnProperty(value = "spring.cluster.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "spring.redis")
public class ContextClusterMasterSlavesConfiguration {

	private String host;
	private String password;
	private int port;
	private int dbIndex;

	@Value("${spring.application.name}")
	private String applicationName;

	@Bean
	@ConditionalOnMissingBean(InstanceIdGenerator.class)
	public InstanceIdGenerator instanceIdGenerator() {
		return new DefaultInstanceIdGenerator();
	}

	@Bean
	@ConditionalOnMissingBean(InstanceId.class)
	public InstanceId instanceId() {
		return new InstanceId(instanceIdGenerator());
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
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
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

	@Bean
	@ConditionalOnMissingBean(RedisMessageListenerContainer.class)
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		return redisMessageListenerContainer;
	}

	@Bean
	@ConditionalOnMissingBean(KeyExpirationEventMessageListener.class)
	public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
			RedisMessageListenerContainer redisMessageListenerContainer) {
		KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
		listener.setKeyspaceNotificationsConfigParameter("Ex");
		return listener;
	}

	@Bean(name = "masterSlavesHeartbeatTask", destroyMethod = "stop")
	public MasterSlavesHeartbeatTask heartbeatTask() {
		return new MasterSlavesHeartbeatTask();
	}

	@Bean
	public ContextClusterMasterSlavesAware contextClusterAware() {
		return new ContextClusterMasterSlavesAware();
	}

	@Bean
	public ContextClusterMasterOfflineListener masterOfflineListener() {
		return new ContextClusterMasterOfflineListener();
	}
}
