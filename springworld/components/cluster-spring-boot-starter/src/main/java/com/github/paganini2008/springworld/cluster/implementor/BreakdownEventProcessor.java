package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

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

	@Override
	public void onMessage(Object message) {
		final String instanceId = (String) message;
		multicastGroup.removeChannel(instanceId);
		multicastEventListener.fireOnLeave(instanceId);
	}

	@Override
	public String getChannel() {
		return ContextMulticastEventNames.BREAKDOWN;
	}

	@Override
	public boolean isEphemeral() {
		return true;
	}

}
