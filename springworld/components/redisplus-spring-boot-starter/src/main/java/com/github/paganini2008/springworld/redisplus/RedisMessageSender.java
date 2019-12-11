package com.github.paganini2008.springworld.redisplus;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 
 * RedisMessageSender
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public class RedisMessageSender {

	static final String EXPIRED_KEY_PREFIX = "__";

	@Value("${spring.redis.pubsub.channel:pubsub}")
	private String channel;

	@Value("${spring.redis.ephemeral-key.namespace:ephemeral:}")
	private String namespace;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	@Qualifier("redis-message-listener")
	private RedisMessageListener redisMessageListener;

	@Autowired
	@Qualifier("redis-ephemeral-message-listener")
	private RedisEphemeralMessageListener redisEphemeralMessageListener;

	public void sendMessage(String channel, Object message) {
		String json = JacksonUtils.toJsonString(RedisMessageEntity.of(channel, message));
		redisTemplate.convertAndSend(this.channel, json);
	}

	public void sendEphemeralMessage(String channel, Object message, long delay, TimeUnit timeUnit) {
		String expiredKey = namespace + channel;
		String json = JacksonUtils.toJsonString(RedisMessageEntity.of(channel, message));
		redisTemplate.opsForValue().set(expiredKey, json, delay, timeUnit);
		setExpiredValue(expiredKey);
	}

	private void setExpiredValue(String expiredKey) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		String jsonResult = redisTemplate.opsForValue().get(expiredKey);
		redisTemplate.opsForValue().set(key, jsonResult);
	}

	public void subscribeChannel(String name, RedisMessageHandler messageListener) {
		if (messageListener.isEphemeral()) {
			redisEphemeralMessageListener.addHandler(name, messageListener);
		} else {
			redisMessageListener.addHandler(name, messageListener);
		}
	}

}
