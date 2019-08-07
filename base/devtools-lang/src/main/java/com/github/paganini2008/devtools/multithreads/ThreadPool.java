package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.Executor;

/**
 * 
 * ThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2012-01
 * @version 1.0
 */
public interface ThreadPool extends Executor {

	default void execute(Runnable task) {
		apply(task);
	}

	boolean apply(Runnable task);

	<R> Promise<R> submit(Action<R> action);

	int getPoolSize();

	int getMaxPoolSize();

	int getQueueSize();

	int getActiveThreadSize();

	int getIdleThreadSize();

	long getCompletedTaskCount();

	long getFailedTaskCount();

	boolean isShutdown();

	void shutdown();

	void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);

}