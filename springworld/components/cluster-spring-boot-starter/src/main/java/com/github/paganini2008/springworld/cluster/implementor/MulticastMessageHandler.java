package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

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
	
	@Autowired
	private MulticastChannelListener multicastChannelListener;

	public void onMessage(String channel, Object message) {
		multicastChannelListener.onData(message);
	}

}
