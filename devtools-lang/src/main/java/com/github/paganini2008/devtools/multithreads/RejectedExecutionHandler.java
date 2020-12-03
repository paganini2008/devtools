package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * RejectedExecutionHandler
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface RejectedExecutionHandler {

	void handleRejectedExecution(Runnable r, ThreadPool threadPool);

}
