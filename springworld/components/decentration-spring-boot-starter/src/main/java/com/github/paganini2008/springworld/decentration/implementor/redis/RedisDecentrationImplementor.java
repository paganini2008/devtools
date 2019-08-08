package com.github.paganini2008.springworld.decentration.implementor.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.springworld.decentration.DecentrationImplementor;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * RedisDecentrationImplementor
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "spring.cluster.decentration.implementor", havingValue = "redis")
public class RedisDecentrationImplementor implements DecentrationImplementor {

	@Value("${spring.redis.host:localhost}")
	private String host;
	@Value("${spring.redis.password:123456}")
	private String password;
	@Value("${spring.redis.port:6379}")
	private int port;
	@Value("${spring.redis.dbIndex:0}")
	private int dbIndex;

	@Value("${spring.application.name}")
	private String applicationName;

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

	@Bean("long")
	public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<String, Long>();
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
		redisTemplate.setExposeConnection(true);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean("ticket")
	public RedisAtomicLong ticket(@Qualifier("long") RedisTemplate<String, Long> redisTemplate) {
		return new RedisAtomicLong("counter:ticket", redisTemplate);
	}

	@Bean("serial")
	public RedisAtomicLong serial(@Qualifier("long") RedisTemplate<String, Long> redisTemplate, StringRedisTemplate stringRedisTemplate,
			@Qualifier("ticket") RedisAtomicLong ticket) {
		RedisAtomicLong serial = new RedisAtomicLong("counter:serial", redisTemplate);
		final String key = ContextHeartbeatEventListener.HEART_BEAT_KEY + applicationName;
		if (stringRedisTemplate.hasKey(key)) {
			log.info("Current context serial number is " + serial.get());
		} else {
			serial.set(ticket.get());
			log.info("Reset context serial number to " + serial.get());
		}
		return serial;
	}

	@Bean
	public ContextHeartbeatAware contextHeartbeatAware() {
		return new ContextHeartbeatAware();
	}

	@Bean
	public ContextHeartbeatEventListener contextHeartbeatEventListener() {
		return new ContextHeartbeatEventListener();
	}

	@Bean
	public HeartbeatKeyExpiredListener heartbeatKeyExpiredListener() {
		return new HeartbeatKeyExpiredListener();
	}
}
