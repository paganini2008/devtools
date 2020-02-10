package com.github.paganini2008.springworld.redis.concurrents;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Lifespan
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface Lifespan {

	void watch(String key, long timeout, long checkInterval, TimeUnit timeUnit);

	void expire(String key);

}
