package com.github.paganini2008.springworld.cluster.implementor;

/**
 * 
 * ContextMulticastEventHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface ContextMulticastEventHandler {

	default void onJoin(String instanceId) {
	}

	default void onLeave(String instanceId) {
	}

	default void onMessage(String instanceId, String message) {
	}

	default String getTopic() {
		return ContextMulticastEventListener.GLOBAL_TOPIC;
	}

}
