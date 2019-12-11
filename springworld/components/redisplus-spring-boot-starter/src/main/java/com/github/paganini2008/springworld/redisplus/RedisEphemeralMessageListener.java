package com.github.paganini2008.springworld.redisplus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.collection.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisEphemeralMessageListener
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("all")
public class RedisEphemeralMessageListener implements ApplicationListener<RedisKeyExpiredEvent> {

	private final ConcurrentMap<String, Map<String, RedisMessageHandler>> channelHandlers = new ConcurrentHashMap<String, Map<String, RedisMessageHandler>>();

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${spring.redis.ephemeral-key.namespace:ephemeral:}")
	private String namespace;

	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		if (expiredKey.startsWith(namespace)) {
			if (log.isTraceEnabled()) {
				log.trace("Key: {} is expired.", expiredKey);
			}
			final String channel = expiredKey.replace(namespace, "");
			if (log.isTraceEnabled()) {
				log.trace("Data into channel '{}'.", channel);
			}
			final Object expiredValue = getExpiredValue(expiredKey);
			Map<String, RedisMessageHandler> handlers = channelHandlers.get(channel);
			if (handlers != null) {
				for (RedisMessageHandler handler : handlers.values()) {
					try {
						handler.onMessage(expiredValue);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	public void addHandler(String name, RedisMessageHandler handler) {
		addHandler("*", name, handler);
	}

	public void addHandler(String channel, String name, RedisMessageHandler handler) {
		Map<String, RedisMessageHandler> handlers = MapUtils.get(channelHandlers, channel, () -> {
			return new ConcurrentHashMap<String, RedisMessageHandler>();
		});
		handlers.putIfAbsent(name, handler);
	}

	private Object getExpiredValue(String expiredKey) {
		final String key = RedisMessageSender.EXPIRED_KEY_PREFIX + expiredKey;
		if (redisTemplate.hasKey(key)) {
			String jsonResult = redisTemplate.opsForValue().get(key);
			redisTemplate.expire(key, 3, TimeUnit.SECONDS);
			RedisMessageEntity entity = JacksonUtils.parseJson(jsonResult, RedisMessageEntity.class);
			return entity.getMessage();
		}
		return null;
	}
}
