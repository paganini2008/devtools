package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Action
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Action<R> {

	R execute() throws Exception;

	default boolean shouldReact(R result) {
		return result != null;
	}

	default R onReaction(R result, ThreadPool threadPool) {
		return null;
	}

	default void onFailure(Exception e, ThreadPool threadPool) {
		e.printStackTrace();
	}
}
