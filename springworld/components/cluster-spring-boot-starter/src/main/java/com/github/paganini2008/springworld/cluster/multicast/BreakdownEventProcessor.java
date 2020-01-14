package com.github.paganini2008.springworld.cluster.multicast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.redisplus.RedisMessageHandler;

/**
 * 
 * BreakdownEventProcessor
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class BreakdownEventProcessor implements RedisMessageHandler {

	@Autowired
	private ContextMulticastGroup multicastGroup;

	@Autowired
	private ContextMulticastEventListener multicastEventListener;

	@Value("${spring.application.cluster.namespace:application:cluster:}")
	private String namespace;

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void onMessage(Object message) {
		final String instanceId = (String) message;
		multicastGroup.removeChannel(instanceId);
		multicastEventListener.fireOnLeave(instanceId);
	}

	@Override
	public String getChannel() {
		return namespace + applicationName + ":multicast:*";
	}

	@Override
	public boolean isEphemeral() {
		return true;
	}

}
