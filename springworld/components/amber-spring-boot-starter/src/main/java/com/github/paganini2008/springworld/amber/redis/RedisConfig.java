package com.github.paganini2008.springworld.amber.redis;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 
 * RedisConfig
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Configuration
@ConditionalOnProperty(prefix = "amber.dispatcher", name = "type", havingValue = "redis")
public class RedisConfig {

	private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

	@Value("${amber.redis.host}")
	private String host;
	@Value("${amber.redis.password}")
	private String password;
	@Value("${amber.redis.port}")
	private int port;
	@Value("${amber.redis.dbIndex:0}")
	private int dbIndex;

	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setDatabase(dbIndex);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		jedisClientConfiguration.connectTimeout(Duration.ofMillis(60000));
		JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
		return factory;
	}

	@ConditionalOnMissingBean(StringRedisTemplate.class)
	@Bean
	public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
		return redisTemplate;
	}

	@Bean
	public RedisDispatcher redisDispatcher() {
		return new RedisDispatcher();
	}

	@Bean
	public RedisClientQueue clientQueue() {
		return new RedisClientQueue();
	}

	@Bean
	public RedisJobProcessor redisJobProcessor() {
		return new RedisJobProcessor();
	}
}
