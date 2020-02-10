package com.github.paganini2008.springworld.redis.pubsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;

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
	private final ConcurrentMap<String, Map<String, RedisMessageHandler>> channelPatternHandlers = new ConcurrentHashMap<String, Map<String, RedisMessageHandler>>();

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

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
			for (String keyPattern : channelPatternHandlers.keySet()) {
				if (matchesChannel(keyPattern, channel)) {
					handlers = channelPatternHandlers.get(keyPattern);
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
		}
	}

	private boolean matchesChannel(String keyPattern, String channel) {
		String key;
		int index = keyPattern.lastIndexOf('*');
		if (index == keyPattern.length() - 1) {
			key = keyPattern.substring(0, index);
			return channel.startsWith(key);
		} else if (index == 0) {
			key = keyPattern.substring(1);
			return channel.endsWith(key);
		} else {
			String[] args = keyPattern.split("\\*");
			for (String arg : args) {
				if (!channel.contains(arg)) {
					return false;
				}
			}
			return true;
		}
	}

	private String getCheckedChannel(String channel) {
		return channel.replaceAll("[\\*]+", "*");
	}

	public void addHandler(String name, RedisMessageHandler handler) {
		Map<String, RedisMessageHandler> handlers;
		String channel = handler.getChannel();
		if (channel.contains("*")) {
			channel = getCheckedChannel(channel);
			handlers = MapUtils.get(channelPatternHandlers, channel, () -> {
				return new ConcurrentHashMap<String, RedisMessageHandler>();
			});
		} else {
			handlers = MapUtils.get(channelHandlers, channel, () -> {
				return new ConcurrentHashMap<String, RedisMessageHandler>();
			});
		}
		handlers.putIfAbsent(name, handler);
	}

	private Object getExpiredValue(String expiredKey) {
		final String key = RedisMessageSender.EXPIRED_KEY_PREFIX + expiredKey;
		if (redisTemplate.hasKey(key)) {
			RedisMessageEntity entity = (RedisMessageEntity) redisTemplate.opsForValue().get(key);
			redisTemplate.expire(key, 3, TimeUnit.SECONDS);
			return entity.getMessage();
		}
		return null;
	}
}
