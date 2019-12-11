package com.github.paganini2008.springworld.cluster.implementor;

import static com.github.paganini2008.springworld.cluster.implementor.ContextMulticastEventNames.STANDBY;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.redisplus.RedisMessageHandler;
import com.github.paganini2008.springworld.redisplus.RedisMessageSender;

/**
 * 
 * StandbyEventProcessor
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class StandbyEventProcessor implements RedisMessageHandler {

	@Autowired
	private RedisMessageSender messageSender;

	@Autowired
	private ContextMulticastGroup multicastGroup;

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private ContextMulticastEventListener multicastEventListener;

	@Override
	public void onMessage(Object message) {
		final String thatId = (String) message;
		if (!multicastGroup.hasRegistered(thatId)) {
			multicastGroup.registerChannel(thatId, 1);
			messageSender.sendMessage(STANDBY, instanceId.get());

			multicastEventListener.fireOnJoin(thatId);
		}
	}

	@Override
	public String getChannel() {
		return STANDBY;
	}

}
