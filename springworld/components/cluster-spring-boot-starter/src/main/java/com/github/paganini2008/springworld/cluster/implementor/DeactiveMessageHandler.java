package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.ApplicationContextUtils;
import com.github.paganini2008.springworld.cluster.redis.RedisMessageHandler;
import com.github.paganini2008.springworld.cluster.redis.RedisMessagePubSub;

/**
 * 
 * DeactiveMessageHandler
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class DeactiveMessageHandler implements RedisMessageHandler {

	@Autowired
	private ContextClusterMulticastChannelGroup channelGroup;

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Override
	public void onMessage(String channel, Object message) {
		final String instanceId = (String) message;
		channelGroup.removeChannel(instanceId);
		redisMessager.subcribeEphemeralChannel(this);

		ApplicationContextUtils.getBeansOfType(MulticastChannelListener.class).values().forEach(multicastChannelListener -> {
			multicastChannelListener.onLeave(instanceId);
		});

	}

}
