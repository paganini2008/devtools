package com.allyes.mec.common.redis;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.allyes.mec.common.utils.ApplicationContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisKeyExpiredListener
 * 
 * @author Fred Feng
 * @created 2019-03
 */
@Slf4j
@Component
@SuppressWarnings("rawtypes")
public class RedisKeyExpiredListener implements ApplicationListener<RedisKeyExpiredEvent>, RedisKeyExpiredEventHandler {

	public static final String EXPIRED_KEY_PREFIX = "__";
	private final ConcurrentMap<String, List<RedisKeyExpiredCallback>> callbacks = new ConcurrentHashMap<String, List<RedisKeyExpiredCallback>>();

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		List<RedisKeyExpiredCallback> expiredCallbacks = callbacks.get(expiredKey);
		if (expiredCallbacks != null) {
			final Object expiredValue = getExpiredValue(expiredKey);
			for (RedisKeyExpiredCallback expiredCallback : expiredCallbacks) {
				ApplicationContextUtils.autowireBean(expiredCallback);
				try {
					expiredCallback.handleKeyExpired(expiredKey, expiredValue);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				} finally {
					expiredCallbacks.remove(expiredCallback);
				}
			}
		}
	}

	private void setExpiredValue(String expiredKey) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		String jsonResult = redisTemplate.opsForValue().get(expiredKey);
		redisTemplate.opsForValue().set(key, jsonResult);
	}

	private String getExpiredValue(String expiredKey) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		String value = redisTemplate.opsForValue().get(key);
		redisTemplate.delete(key);
		return value;
	}

	public void registerCallback(String key, RedisKeyExpiredCallback keyExpiredCallback) {
		if (keyExpiredCallback != null) {
			List<RedisKeyExpiredCallback> expiredHandlers = callbacks.get(key);
			if (expiredHandlers == null) {
				callbacks.putIfAbsent(key, new CopyOnWriteArrayList<RedisKeyExpiredCallback>());
				expiredHandlers = callbacks.get(key);
			}
			if (!expiredHandlers.contains(keyExpiredCallback)) {
				expiredHandlers.add(keyExpiredCallback);
				setExpiredValue(key);
			}
		}
	}

	public void registerCallback(String key, int index, RedisKeyExpiredCallback keyExpiredCallback) {
		if (keyExpiredCallback != null) {
			List<RedisKeyExpiredCallback> expiredHandlers = callbacks.get(key);
			if (expiredHandlers == null) {
				callbacks.putIfAbsent(key, new CopyOnWriteArrayList<RedisKeyExpiredCallback>());
				expiredHandlers = callbacks.get(key);
			}
			if (!expiredHandlers.contains(keyExpiredCallback)) {
				expiredHandlers.add(index, keyExpiredCallback);
				setExpiredValue(key);
			}
		}
	}

}
