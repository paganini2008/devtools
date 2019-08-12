package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.context.ApplicationContext;

import com.github.paganini2008.springworld.cluster.ContextMulticastEvent;

/**
 * 
 * MulticastMessageHandler
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class MulticastMessageHandler implements RedisMessageHandler {

	private final ApplicationContext applicationContext;

	public MulticastMessageHandler(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void handleMessage(String channel, Object message) {
		applicationContext.publishEvent(new ContextMulticastEvent(applicationContext));
	}

}
