package com.github.paganini2008.springworld.cluster.implementor;

import com.github.paganini2008.springworld.cluster.ApplicationContextUtils;
import com.github.paganini2008.springworld.cluster.redis.RedisMessageHandler;

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

	public void onMessage(String channel, Object message) {
		ApplicationContextUtils.getBeansOfType(MulticastChannelListener.class).values().forEach(multicastChannelListener -> {
			multicastChannelListener.onData(message);
		});
	}

}
