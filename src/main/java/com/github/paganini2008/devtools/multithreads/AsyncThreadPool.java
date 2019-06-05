package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * AsyncThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2016-12
 * @version 1.0
 */
public interface AsyncThreadPool<T> extends ThreadPool {

	boolean submit(Execution<T> execution);

	Promise<T> submitAndWait(Execution<T> execution);

}
