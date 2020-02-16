package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Executable
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
@FunctionalInterface
public interface Executable {

	boolean execute();

	default boolean onError(Exception e) {
		e.printStackTrace();
		return false;
	}

	default void onCancellation() {
	}

}
