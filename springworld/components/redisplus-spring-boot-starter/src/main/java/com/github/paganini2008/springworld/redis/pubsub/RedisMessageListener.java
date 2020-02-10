package com.github.paganini2008.springworld.redis.pubsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.collection.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisMessageListener
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class RedisMessageListener implements ApplicationListener<RedisMessageEvent> {

	private final Map<String, Map<String, RedisMessageHandler>> channelHandlers = new ConcurrentHashMap<String, Map<String, RedisMessageHandler>>();

	public void onApplicationEvent(RedisMessageEvent event) {
		final String channel = event.getChannel();
		if (log.isTraceEnabled()) {
			log.trace("Data into channel '{}'.", channel);
		}
		final Object message = event.getMessage();
		Map<String, RedisMessageHandler> handlers = channelHandlers.get(channel);
		if (handlers != null) {
			for (RedisMessageHandler handler : handlers.values()) {
				try {
					handler.onMessage(message);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	public void addHandler(String name, RedisMessageHandler handler) {
		String channel = handler.getChannel();
		Map<String, RedisMessageHandler> handlers = MapUtils.get(channelHandlers, channel, () -> {
			return new ConcurrentHashMap<String, RedisMessageHandler>();
		});
		handlers.putIfAbsent(name, handler);
	}

}
