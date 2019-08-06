package com.github.paganini2008.springworld.support.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.support.JacksonUtils;

/**
 * 
 * RedisMessageSender
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2019-07
 * @version 1.0
 */
public class RedisMessageSender {

	@Value("${redis.pubsub.channel:defaultChannel}")
	private String channel;

	@Autowired
	private StringRedisTemplate redisTemplate;

	public void sendMessage(String channel, Object message) {
		String json = JacksonUtils.toJsonString(MessageEntity.of(channel, message));
		redisTemplate.convertAndSend(this.channel, json);
	}

	public void sendDelayMessage(String channel, Object message, long delay, TimeUnit timeUnit) {
		String json = JacksonUtils.toJsonString(MessageEntity.of(channel, message));
		redisTemplate.opsForValue().set(channel, json, delay, timeUnit);
	}

	public void executeDelayMessage(String channel) {
		if (redisTemplate.hasKey(channel)) {
			redisTemplate.expire(channel, 1, TimeUnit.SECONDS);
		}
	}

	public void cancelDelayMessage(String channel) {
		if (redisTemplate.hasKey(channel)) {
			redisTemplate.delete(RedisKeyExpiredEventListener.EXPIRED_KEY_PREFIX + channel);
			redisTemplate.delete(channel);
		}
	}

}
