package com.github.paganini2008.springworld.cluster.implementor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * LoggingContextMulticastEventHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class LoggingContextMulticastEventHandler implements ContextMulticastEventHandler {

	@Override
	public void onJoin(String instanceId) {
		if (log.isTraceEnabled()) {
			log.trace("Application '{}' joined.", instanceId);
		}
	}

	@Override
	public void onLeave(String instanceId) {
		if (log.isTraceEnabled()) {
			log.trace("Application '{}' left.", instanceId);
		}
	}

	@Override
	public void onMessage(String instanceId, String message) {
		if (log.isTraceEnabled()) {
			log.trace("Application '{}' send: {}", instanceId, message);
		}
	}

}
