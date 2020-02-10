package com.github.paganini2008.springworld.redis.pubsub;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

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
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	@Qualifier("redis-message-listener")
	private RedisMessageListener redisMessageListener;

	@Autowired
	@Qualifier("redis-ephemeral-message-listener")
	private RedisEphemeralMessageListener redisEphemeralMessageListener;

	public void sendMessage(String channel, Object message) {
		redisTemplate.convertAndSend(this.channel, RedisMessageEntity.of(channel, message));
	}

	public void sendEphemeralMessage(String channel, Object message, long delay, TimeUnit timeUnit) {
		sendEphemeralMessage(channel, message, delay, timeUnit, false);
	}

	public void sendEphemeralMessage(String channel, Object message, long delay, TimeUnit timeUnit, boolean idempotent) {
		String expiredKey = namespace + channel;
		if (!idempotent || redisTemplate.hasKey(expiredKey)) {
			RedisMessageEntity entity = RedisMessageEntity.of(channel, message);
			redisTemplate.opsForValue().set(expiredKey, entity, delay, timeUnit);
			setExpiredValue(expiredKey);
		}
	}

	private void setExpiredValue(String expiredKey) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		Object value = redisTemplate.opsForValue().get(expiredKey);
		redisTemplate.opsForValue().set(key, value);
	}

	public void subscribeChannel(String name, RedisMessageHandler messageHandler) {
		if (messageHandler.isEphemeral()) {
			redisEphemeralMessageListener.addHandler(name, messageHandler);
		} else {
			redisMessageListener.addHandler(name, messageHandler);
		}
	}

}
