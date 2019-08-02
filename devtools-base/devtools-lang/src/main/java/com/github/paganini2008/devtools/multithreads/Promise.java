package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Promise
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2019-05
 * @version 1.0
 */
public interface Promise<R> {

	boolean isCancelled();

	boolean isDone();

	long getElapsed();

	R get();

	R get(long timeout);

	void cancel();

}
