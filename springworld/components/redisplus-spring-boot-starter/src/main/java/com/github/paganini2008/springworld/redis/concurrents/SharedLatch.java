package com.github.paganini2008.springworld.redis.concurrents;

import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * SharedLatch
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface SharedLatch extends Latch, KeepAliveHandler {

	String getKey();

}
