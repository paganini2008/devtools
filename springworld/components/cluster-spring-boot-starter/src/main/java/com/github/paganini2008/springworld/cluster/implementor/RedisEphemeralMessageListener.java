package com.github.paganini2008.springworld.cluster.implementor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

	@Value("${spring.redis.pubsub.keyexpired.namespace:springboot:cluster:multicast:member:}")
	private String namespace;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private AutowireCapableBeanFactory beanFactory;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		if (log.isTraceEnabled()) {
			log.trace("Key: {} is expired.");
		}
		if (expiredKey.startsWith(namespace)) {
			final String channel = expiredKey.replace(namespace, "");
			if (log.isTraceEnabled()) {
				log.trace("Data into channel '{}'.", channel);
			}
			handleMessage(expiredKey, channel);
			handleMessage(expiredKey, "*");
		}
	}

	private void handleMessage(String expiredKey, String channel) {
		List<RedisMessageHandler> messageHandlers = channelHandlers.get(channel);
		if (messageHandlers != null) {
			final Object expiredValue = getExpiredValue(expiredKey);
			for (RedisMessageHandler redisMessageHandler : messageHandlers) {
				try {
					beanFactory.autowireBean(redisMessageHandler);
					redisMessageHandler.handleMessage(expiredKey, expiredValue);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				} finally {
					messageHandlers.remove(redisMessageHandler);
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
		String jsonResult = redisTemplate.opsForValue().get(key);
		redisTemplate.delete(key);
		MessageEntity entity = JacksonUtils.parseJson(jsonResult, MessageEntity.class);
		return entity.getMessage();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}
}
