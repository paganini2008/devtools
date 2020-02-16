package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Promise
 * 
 * @author Fred Feng
 * 
 * 
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
