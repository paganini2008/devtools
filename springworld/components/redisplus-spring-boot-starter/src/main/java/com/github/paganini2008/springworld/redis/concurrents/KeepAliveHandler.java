package com.github.paganini2008.springworld.redis.concurrents;

/**
 * 
 * KeepAliveHandler
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface KeepAliveHandler {

	void keepAlive(Lifespan lifespan, int checkInterval);
	
}
