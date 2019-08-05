package com.allyes.mec.common.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * RedisMessageEventPublisher
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class RedisMessageEventPublisher implements ApplicationContextAware {

	private ApplicationContext springContext;

	@Value("${hlsh.redis.mq.defaultChannel:defaultChannel}")
	private String channel;

	@Override
	public void setApplicationContext(ApplicationContext springContext) throws BeansException {
		this.springContext = springContext;
	}

	public void publish(final String message) {
		RedisMessageEvent event = new RedisMessageEvent(channel, message);
		springContext.publishEvent(event);
	}

}
