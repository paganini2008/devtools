package com.github.paganini2008.springworld.cluster.multicast;

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

	default void onJoin(String clusterId) {
	}

	default void onLeave(String clusterId) {
	}

	default void onMessage(String clusterId, Object message) {
	}

	default void onGlobalMessage(String clusterId, Object message) {
	}

	default String getTopic() {
		return ContextMulticastEventListener.GLOBAL_TOPIC;
	}

}
