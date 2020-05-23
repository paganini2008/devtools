package com.github.paganini2008.devtools;

/**
 * 
 * Acceptable
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Acceptable {

	boolean accept();

	default void exceptionCaught(Throwable e, int retrying) {
	}
	
	default long retryingInterval() {
		return 1000L;
	}

	default int retries() {
		return 0;
	}
	
}
