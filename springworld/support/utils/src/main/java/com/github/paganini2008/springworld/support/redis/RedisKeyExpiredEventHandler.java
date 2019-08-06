package com.github.paganini2008.springworld.support.redis;

/**
 * 
 * RedisKeyExpiredEventHandler
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-06
 * @version 1.0
 */
public interface RedisKeyExpiredEventHandler {

	void registerCallback(String key, RedisKeyExpiredCallback keyExpiredCallback);

	void registerCallback(String key, int index, RedisKeyExpiredCallback keyExpiredCallback);
}
