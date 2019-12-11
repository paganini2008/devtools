package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Value;

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

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void onJoin(String instanceId) {
		if (log.isTraceEnabled()) {
			log.trace("Application '{}/{}' joined.", applicationName, instanceId);
		}
	}

	@Override
	public void onLeave(String instanceId) {
		if (log.isTraceEnabled()) {
			log.trace("Application '{}/{}' left.", applicationName, instanceId);
		}
	}

	@Override
	public void onMessage(String instanceId, String message) {
		if (log.isTraceEnabled()) {
			log.trace("Application '{}/{}' send: {}", applicationName, instanceId, message);
		}
	}

}
