package com.github.paganini2008.redistools;

/**
 * 
 * RedisMessageHandler
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface RedisMessageHandler {
	
	void handleMessage(String channel, String message);
	
}
