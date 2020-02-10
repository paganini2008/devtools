package com.github.paganini2008.springworld.redis.pubsub;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * RedisMessageEventPublisher
 * 
 * @author Fred Feng
 * @created 2019-07
 * @revised 2019-05
 * @version 1.0
 */
public class RedisMessageEventPublisher implements ApplicationContextAware {

	private ApplicationContext springContext;

	@Override
	public void setApplicationContext(ApplicationContext springContext) throws BeansException {
		this.springContext = springContext;
	}

	public void publish(final RedisMessageEntity entity) {
		RedisMessageEvent event = new RedisMessageEvent(entity.getChannel(), entity.getMessage());
		springContext.publishEvent(event);
	}

}
