package com.github.paganini2008.redistools;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * RedisMessageSender
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
@Component
public class RedisMessageSender {

	@Value("${hlsh.redis.mq.defaultChannel:defaultChannel}")
	private String channel;

	@Autowired
	private StringRedisTemplate redisTemplate;

	public void sendMessage(String channel, String message) {
		String json = JsonUtils.parseObject(MessageEntry.of(channel, message));
		redisTemplate.convertAndSend(this.channel, json);
	}

	public void sendDelayMessage(String channel, String message, long delay, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(RedisKeyExpiredListener.EXPIRED_KEY_PREFIX + channel, message);
		redisTemplate.opsForValue().set(channel, message, delay, timeUnit);
	}

	public String executeDelayMessage(String channel) {
		String value = null;
		if (redisTemplate.hasKey(channel)) {
			value = redisTemplate.opsForValue().get(channel);
			redisTemplate.expire(channel, 1, TimeUnit.SECONDS);
		}
		return value;
	}

	public String cancelDelayMessage(String channel) {
		String value = null;
		if (redisTemplate.hasKey(channel)) {
			value = redisTemplate.opsForValue().get(channel);
			redisTemplate.delete(RedisKeyExpiredListener.EXPIRED_KEY_PREFIX + channel);
			redisTemplate.delete(channel);
		}
		return value;
	}

	public StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	@Getter
	@Setter
	@ToString
	public static class MessageEntry implements Serializable {

		private static final long serialVersionUID = 1L;
		private final String channel;
		private final String message;

		MessageEntry(String channel, String message) {
			this.channel = channel;
			this.message = message;
		}

		public static MessageEntry of(String channel, String message) {
			return new MessageEntry(channel, message);
		}

	}

}
