package com.github.paganini2008.springworld.cluster.implementor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.cluster.JacksonUtils;

/**
 * 
 * RedisMessagePubSub
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public class RedisMessagePubSub {

	public static final String EXPIRED_KEY_PREFIX = "__";

	@Value("${spring.redis.pubsub.channel:defaultChannel}")
	private String channel;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	@Qualifier("cluster-message-listener")
	private RedisMessageListener redisMessageListener;

	@Autowired
	@Qualifier("cluster-ephemeral-message-listener")
	private RedisEphemeralMessageListener redisEphemeralMessageListener;

	public void sendMessage(String channel, Object message) {
		String json = JacksonUtils.toJsonString(MessageEntity.of(channel, message));
		redisTemplate.convertAndSend(this.channel, json);
	}

	public void sendEphemeralMessage(String channel, Object message, long delay, TimeUnit timeUnit) {
		String json = JacksonUtils.toJsonString(MessageEntity.of(channel, message));
		redisTemplate.opsForValue().set(channel, json, delay, timeUnit);
		setExpiredValue(channel);
	}

	private void setExpiredValue(String expiredKey) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		String jsonResult = redisTemplate.opsForValue().get(expiredKey);
		redisTemplate.opsForValue().set(key, jsonResult);
	}

	public void subcribeChannel(String channel, RedisMessageHandler messageHandler) {
		redisMessageListener.registerMessageHandler(channel, messageHandler);
	}

	public void subcribeEphemeralChannel(RedisMessageHandler messageHandler) {
		redisEphemeralMessageListener.registerMessageHandler(messageHandler);
	}

	public void subcribeEphemeralChannel(String channel, RedisMessageHandler messageHandler) {
		redisEphemeralMessageListener.registerMessageHandler(channel, messageHandler);
	}

}
