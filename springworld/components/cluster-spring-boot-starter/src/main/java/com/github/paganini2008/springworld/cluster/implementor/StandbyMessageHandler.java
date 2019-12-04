package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.ApplicationContextUtils;
import com.github.paganini2008.springworld.cluster.redis.RedisMessageHandler;
import com.github.paganini2008.springworld.cluster.redis.RedisMessagePubSub;

/**
 * 
 * StandbyMessageHandler
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class StandbyMessageHandler implements RedisMessageHandler {

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Autowired
	private ContextClusterMulticastChannelGroup channelGroup;

	@Autowired
	private InstanceId instanceId;

	@Override
	public void onMessage(String channel, Object message) {
		final String selfId = instanceId.get();
		final String otherId = (String) message;
		if (!channelGroup.hasRegistered(otherId)) {
			channelGroup.registerChannel(otherId, 1);
			redisMessager.sendMessage("standby", selfId);

			ApplicationContextUtils.getBeansOfType(MulticastChannelListener.class).values().forEach(multicastChannelListener -> {
				multicastChannelListener.onJoin(otherId);
			});
		}
	}

}
