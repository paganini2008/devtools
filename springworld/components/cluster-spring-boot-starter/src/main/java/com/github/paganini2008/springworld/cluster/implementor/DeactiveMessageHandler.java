package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DeactiveMessageHandler
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class DeactiveMessageHandler implements RedisMessageHandler {

	@Autowired
	private ContextMulticastChannelGroup channelGroup;

	@Override
	public void handleMessage(String channel, Object message) {
		channelGroup.removeChannel((String) message);
		log.info("Remove channel: " + message);
	}

}
