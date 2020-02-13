package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * RejectedExecutionHandler
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2014-03
 * @version 1.0
 */
public interface RejectedExecutionHandler {

	void handleRejectedExecution(Runnable r, ThreadPool threadPool);

}
