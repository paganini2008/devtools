package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * Execution
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface Execution extends Comparable<Execution> {

	Object execute() throws Exception;

	default void onSuccess(Object result, ThreadPool threadPool) {
	}

	default void onFailure(Exception e, ThreadPool threadPool) {
		e.printStackTrace();
	}

	default int compareTo(Execution other) {
		return 0;
	}

	/**
	 * 
	 * RejectedExecutionHandler
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	public interface RejectedExecutionHandler {

		void handleRejectedExecution(Execution execution, ThreadPool threadPool);
	}

}
