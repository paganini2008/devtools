package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * StandbyMessageHandler
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class StandbyMessageHandler implements RedisMessageHandler {

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Autowired
	private ContextMulticastChannelGroup channelGroup;

	@Autowired
	private ContextMulticastAware multicastAware;

	@Override
	public void handleMessage(String channel, Object message) {
		final String self = multicastAware.getId();
		final String other = (String) message;
		if (!self.equals(other)) {
			channelGroup.registerChannel(other, 1);
			redisMessager.sendMessage("standby", self);
			log.info(self + " connect to: " + other);
		}
	}

}
