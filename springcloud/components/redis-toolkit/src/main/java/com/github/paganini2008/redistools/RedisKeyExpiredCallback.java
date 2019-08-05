package com.github.paganini2008.redistools;

/**
 * 
 * RedisKeyExpiredCallback 不用实例化
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public interface RedisKeyExpiredCallback {

	void handleKeyExpired(String expiredKey, Object expiredValue);

}
