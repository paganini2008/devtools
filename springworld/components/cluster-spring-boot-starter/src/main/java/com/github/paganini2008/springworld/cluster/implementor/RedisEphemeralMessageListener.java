package com.github.paganini2008.springworld.cluster.implementor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.cluster.JacksonUtils;

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
public class RedisEphemeralMessageListener implements ApplicationListener<RedisKeyExpiredEvent>, ApplicationContextAware {

	private final ConcurrentMap<String, List<RedisMessageHandler>> channelHandlers = new ConcurrentHashMap<String, List<RedisMessageHandler>>();
	private final static String CLUSTER_MULTICAST_KEY_PREFIX = "springboot:cluster:%s:multicast";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${spring.application.name}")
	private String applicationName;

	private AutowireCapableBeanFactory beanFactory;

	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		if (log.isTraceEnabled()) {
			log.trace("Key: {} is expired.", expiredKey);
		}
		String namespace = String.format(CLUSTER_MULTICAST_KEY_PREFIX, applicationName);
		if (expiredKey.startsWith(namespace)) {
			final Object expiredValue = getExpiredValue(expiredKey);
			final String channel = expiredKey.replace(namespace, "");
			if (log.isTraceEnabled()) {
				log.trace("Data into channel '{}'.", channel);
			}
			handleMessage(channel, expiredValue);
			handleMessage("*", expiredValue);
		}
	}

	private void handleMessage(String channel, Object message) {
		List<RedisMessageHandler> messageHandlers = channelHandlers.remove(channel);
		if (messageHandlers != null) {
			for (RedisMessageHandler messageHandler : messageHandlers) {
				try {
					beanFactory.autowireBean(messageHandler);
					messageHandler.onMessage(channel, message);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	public void registerMessageHandler(RedisMessageHandler messageHandler) {
		registerMessageHandler("*", messageHandler);
	}

	public void registerMessageHandler(String channel, RedisMessageHandler messageHandler) {
		if (messageHandler != null) {
			List<RedisMessageHandler> messageHandlers = channelHandlers.get(channel);
			if (messageHandlers == null) {
				channelHandlers.putIfAbsent(channel, new CopyOnWriteArrayList<RedisMessageHandler>());
				messageHandlers = channelHandlers.get(channel);
			}
			if (!messageHandlers.contains(messageHandler)) {
				messageHandlers.add(messageHandler);
			}
		}
	}

	private Object getExpiredValue(String expiredKey) {
		final String key = RedisMessagePubSub.EXPIRED_KEY_PREFIX + expiredKey;
		if (redisTemplate.hasKey(key)) {
			String jsonResult = redisTemplate.opsForValue().get(key);
			redisTemplate.expire(key, 60, TimeUnit.SECONDS);
			MessageEntity entity = JacksonUtils.parseJson(jsonResult, MessageEntity.class);
			return entity.getMessage();
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}
}
