package com.github.paganini2008.springworld.cluster.redis;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.paganini2008.springworld.cluster.JacksonUtils;
import com.github.paganini2008.springworld.cluster.RedisMessageEvent;
import com.github.paganini2008.springworld.cluster.implementor.MessageEntity;

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

	public void publish(final String jsonResult) {
		MessageEntity result = JacksonUtils.parseJson(jsonResult, MessageEntity.class);
		RedisMessageEvent event = new RedisMessageEvent(result.getChannel(), result.getMessage());
		springContext.publishEvent(event);
	}

}
