package com.github.paganini2008.springworld.cluster.redis;

/**
 * 
 * RedisMessageHandler
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public interface RedisMessageHandler {

	void onMessage(String channel, Object message);

}
