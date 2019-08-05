package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Executable
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2013-10
 * @version 1.0
 */
public interface Executable {

	boolean execute();

	default boolean onError(Exception e) {
		e.printStackTrace();
		return false;
	}

	default void onCancellation() {
	}

}
