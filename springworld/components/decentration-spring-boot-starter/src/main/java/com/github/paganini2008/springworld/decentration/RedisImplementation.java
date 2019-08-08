package com.github.paganini2008.springworld.decentration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * RedisImplementation
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
@Slf4j
@Configuration
public class RedisImplementation {

	@Value("${redis.host}")
	private String host;
	@Value("${redis.password}")
	private String password;
	@Value("${redis.port:6379}")
	private int port;
	@Value("${redis.dbIndex:0}")
	private int dbIndex;

	@Bean
	public RedisConnectionFactory createRedisConnectionFactory() {
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
	public StringRedisTemplate createStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMinIdle(1);
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxWaitMillis(-1);
		jedisPoolConfig.setTestWhileIdle(true);
		return jedisPoolConfig;
	}

	@Bean("bigint")
	public RedisTemplate<String, Long> bigintRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<String, Long>();
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
		redisTemplate.setExposeConnection(true);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean("ticket")
	public RedisAtomicLong ticket(@Qualifier("bigint") RedisTemplate<String, Long> redisTemplate) {
		return new RedisAtomicLong("counter:ticket", redisTemplate);
	}

	@Bean("serial")
	public RedisAtomicLong serial(@Qualifier("bigint") RedisTemplate<String, Long> redisTemplate,
			@Qualifier("ticket") RedisAtomicLong atomicLong) {
		RedisAtomicLong serial = new RedisAtomicLong("counter:serial", redisTemplate);
		serial.set(atomicLong.get());
		log.info("Current context serial number is " + serial.get());
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
