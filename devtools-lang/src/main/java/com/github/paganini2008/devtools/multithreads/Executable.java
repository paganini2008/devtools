package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Executable
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Executable {

	boolean execute();

	default boolean onError(Throwable cause) {
		cause.printStackTrace();
		return false;
	}

	default void onCancellation(Throwable cause) {
	}

}
