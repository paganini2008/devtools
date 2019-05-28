package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Latch
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface Latch {

	long getPermits();

	boolean acquire();

	boolean acquire(long timeout, TimeUnit timeUnit);

	void release();

	void join();
}
