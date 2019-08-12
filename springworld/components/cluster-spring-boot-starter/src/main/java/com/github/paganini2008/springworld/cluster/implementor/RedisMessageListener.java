package com.github.paganini2008.springworld.cluster.implementor;

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

import com.github.paganini2008.springworld.cluster.RedisMessageEvent;

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
public class RedisMessageListener implements ApplicationListener<RedisMessageEvent> , ApplicationContextAware{

	private final ConcurrentMap<String, List<RedisMessageHandler>> channelHandlers = new ConcurrentHashMap<String, List<RedisMessageHandler>>();

	@Autowired
	private AutowireCapableBeanFactory beanFactory;
	
	public void onApplicationEvent(RedisMessageEvent event) {
		final String channel = event.getChannel();
		if (log.isTraceEnabled()) {
			log.trace("Data into channel '{}'.", channel);
		}
		final Object message = event.getMessage();
		List<RedisMessageHandler> handlers = channelHandlers.get(channel);
		if (handlers != null) {
			for (RedisMessageHandler handler : handlers) {
				try {
					beanFactory.autowireBean(handler);
					handler.handleMessage(channel, message);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	public void registerMessageHandler(String channel, RedisMessageHandler messageHandler) {
		if (messageHandler != null) {
			List<RedisMessageHandler> handlers = channelHandlers.get(channel);
			if (handlers == null) {
				channelHandlers.putIfAbsent(channel, new CopyOnWriteArrayList<RedisMessageHandler>());
				handlers = channelHandlers.get(channel);
			}
			handlers.add(messageHandler);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

}
