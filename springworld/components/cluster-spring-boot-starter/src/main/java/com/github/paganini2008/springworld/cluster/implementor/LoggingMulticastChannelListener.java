package com.github.paganini2008.springworld.cluster.implementor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * LoggingMulticastChannelListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class LoggingMulticastChannelListener implements MulticastChannelListener {

	@Override
	public void onJoin(String instanceId) {
		log.info("Application: " + instanceId + " joined.");
	}

	@Override
	public void onLeave(String instanceId) {
		log.info("Application: " + instanceId + " left.");
	}

	@Override
	public void onData(Object message) {
		log.info("Application message: " + message);
	}

}
