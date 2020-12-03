package com.github.paganini2008.devtools;

/**
 * 
 * Obtainable
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public interface Obtainable<T> {

	T obtain();

	default void exceptionCaught(Throwable e, int retrying) {
	}
	
	default long retryingInterval() {
		return 1000L;
	}

	default int retries() {
		return 0;
	}

	default T defaultValue() {
		return null;
	}

}
