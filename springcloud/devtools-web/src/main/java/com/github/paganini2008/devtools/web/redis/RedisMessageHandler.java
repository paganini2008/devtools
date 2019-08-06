package com.github.paganini2008.devtools.web.redis;

/**
 * 
 * RedisMessageHandler
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface RedisMessageHandler {
	
	void handleMessage(String channel, Object message);
	
}
