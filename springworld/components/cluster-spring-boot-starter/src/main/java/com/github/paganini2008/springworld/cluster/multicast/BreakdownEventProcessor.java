package com.github.paganini2008.springworld.cluster.multicast;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.ContextClusterConfigProperties;
import com.github.paganini2008.springworld.redis.pubsub.RedisMessageHandler;

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

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Override
	public void onMessage(Object message) {
		final String clusterId = (String) message;
		multicastGroup.removeChannel(clusterId);
		multicastEventListener.fireOnLeave(clusterId);
	}

	@Override
	public String getChannel() {
		return configProperties.getApplicationClusterName() + ":*";
	}

	@Override
	public boolean isEphemeral() {
		return true;
	}

}
