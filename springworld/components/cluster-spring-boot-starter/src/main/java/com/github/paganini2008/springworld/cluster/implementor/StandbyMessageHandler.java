package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.redis.RedisMessageHandler;
import com.github.paganini2008.springworld.cluster.redis.RedisMessagePubSub;

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
	private ContextClusterMulticastChannelGroup channelGroup;

	@Autowired
	private InstanceId instanceId;

	@Override
	public void onMessage(String channel, Object message) {
		final String self = instanceId.get();
		final String otherId = (String) message;
		if (!channelGroup.hasRegistered(otherId)) {
			channelGroup.registerChannel(otherId, 1);
			redisMessager.sendMessage("standby", self);
			log.info(self + " connect to: " + otherId);
		
		}
	}

}
