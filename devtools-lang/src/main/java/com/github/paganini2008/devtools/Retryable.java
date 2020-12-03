package com.github.paganini2008.devtools;

/**
 * 
 * Retryable
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public interface Retryable<T> {

	default boolean tryAccept() {
		return true;
	}

	T accept();

	default void exceptionCaught(Throwable e, int retryCount) {
	}

	default long retryInterval() {
		return 1000L;
	}

	default int maxAttempts() {
		return 0;
	}

}
