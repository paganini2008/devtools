package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Latch
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Latch {

	long availablePermits();

	boolean acquire();
	
	boolean tryAcquire();

	boolean acquire(long timeout, TimeUnit timeUnit);

	void release();

	boolean isLocked();

	long join();
}
