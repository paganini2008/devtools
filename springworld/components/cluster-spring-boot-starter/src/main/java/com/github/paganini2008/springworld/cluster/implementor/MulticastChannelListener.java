package com.github.paganini2008.springworld.cluster.implementor;

/**
 * 
 * MulticastChannelListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface MulticastChannelListener {

	void onJoin(String instanceId);

	void onLeave(String instanceId);
	
	void onData(Object message);

}
