package com.github.paganini2008.springworld.support.redis;

/**
 * 
 * RedisKeyExpiredCallback
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public interface RedisKeyExpiredCallback {

	void handleKeyExpired(String expiredKey, Object expiredValue);

}
