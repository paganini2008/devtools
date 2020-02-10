package com.github.paganini2008.springworld.redis.pubsub;

/**
 * 
 * RedisMessageHandler
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-11
 * @version 1.0
 */
public interface RedisMessageHandler {

	String getChannel();

	void onMessage(Object message);

	default boolean isEphemeral() {
		return false;
	}

}
