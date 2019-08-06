package com.github.paganini2008.springworld.support.redis;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.support.JacksonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisKeyExpiredListener
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class RedisKeyExpiredEventListener
		implements ApplicationContextAware, ApplicationListener<RedisKeyExpiredEvent>, RedisKeyExpiredEventHandler {

	public static final String EXPIRED_KEY_PREFIX = "__";
	private final ConcurrentMap<String, List<RedisKeyExpiredCallback>> callbacks = new ConcurrentHashMap<String, List<RedisKeyExpiredCallback>>();

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		List<RedisKeyExpiredCallback> expiredCallbacks = callbacks.get(expiredKey);
		if (expiredCallbacks != null) {
			final Object expiredValue = getExpiredValue(expiredKey);
			for (RedisKeyExpiredCallback expiredCallback : expiredCallbacks) {
				beanFactory.autowireBean(expiredCallback);
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

	private Object getExpiredValue(String expiredKey) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		String jsonResult = redisTemplate.opsForValue().get(key);
		redisTemplate.delete(key);
		MessageEntity entity = JacksonUtils.parseJson(jsonResult, MessageEntity.class);
		return entity.getMessage();
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

}
