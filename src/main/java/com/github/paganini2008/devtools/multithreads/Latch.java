package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Latch
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2014-03
 * @version 1.0
 */
public interface Latch {

	long availablePermits();

	boolean tryAcquire();

	boolean acquire();

	boolean acquire(long timeout, TimeUnit timeUnit);

	void release();

	boolean isLocked();

	long join();
}
