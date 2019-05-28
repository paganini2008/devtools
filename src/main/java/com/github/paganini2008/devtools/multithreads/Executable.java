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
	
	default void onError(Exception e) {
		e.printStackTrace();
	}
	
	default void onCancellation() {
	}

}
