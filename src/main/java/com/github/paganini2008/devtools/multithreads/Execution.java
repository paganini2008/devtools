package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Execution
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface Execution<T> extends Comparable<Execution<T>> {

	T execute() throws Exception;

	default void onSuccess(T result, AsyncThreadPool<T> threadPool) {
	}
	
	default void onFailure(T result, Exception e, AsyncThreadPool<T> threadPool) {
		e.printStackTrace();
	}

	default void onFailure(Exception e, AsyncThreadPool<T> threadPool) {
		e.printStackTrace();
	}

	default int compareTo(Execution<T> other) {
		return 0;
	}

}
