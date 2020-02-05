package com.github.paganini2008.springworld.cluster.multicast;

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
	public void onJoin(String clusterId) {
		if (log.isTraceEnabled()) {
			log.trace("Spring application '{}' has joined.", clusterId);
		}
	}

	@Override
	public void onLeave(String clusterId) {
		if (log.isTraceEnabled()) {
			log.trace("Spring application '{}' has gone.", clusterId);
		}
	}

	@Override
	public void onMessage(String clusterId, Object message) {
		if (log.isTraceEnabled()) {
			log.trace("Spring application '{}' send message: {}", clusterId, message);
		}
	}

	@Override
	public void onGlobalMessage(String clusterId, Object message) {
		if (log.isTraceEnabled()) {
			log.trace("Spring application '{}' send global message: {}", clusterId, message);
		}
	}

}
