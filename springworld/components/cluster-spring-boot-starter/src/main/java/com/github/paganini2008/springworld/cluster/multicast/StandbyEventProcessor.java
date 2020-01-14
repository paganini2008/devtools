package com.github.paganini2008.springworld.cluster.multicast;

import static com.github.paganini2008.springworld.cluster.multicast.ContextMulticastEventNames.STANDBY;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.InstanceId;
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
		String[] args = ((String) message).split(":", 2);
		final String thatId = args[0];
		final int weight = Integer.parseInt(args[1]);
		if (!multicastGroup.hasRegistered(thatId)) {
			multicastGroup.registerChannel(thatId, weight);
			messageSender.sendMessage(STANDBY, instanceId.get() + ":" + instanceId.getWeight());

			multicastEventListener.fireOnJoin(thatId);
		}
	}

	@Override
	public String getChannel() {
		return STANDBY;
	}

}
