package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.Executor;

import com.github.paganini2008.devtools.multithreads.Execution.RejectedExecutionHandler;

/**
 * 
 * ThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface ThreadPool extends Executor {

	boolean submit(Execution execution);

	Promise<?> submitAndWait(Execution execution);

	long getPermits();
	
	int getQueueSize();
	
	boolean isRunning();

	void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);

	default void execute(final Runnable r) {
		submit(() -> {
			r.run();
			return null;
		});
	}

	default void shutdown() {
	}

	static ThreadPool buildThreadPool(int nThreads) {
		return buildThreadPool(nThreads, Integer.MAX_VALUE);
	}

	static ThreadPool buildThreadPool(int nThreads, int maxPermits) {
		return new ThreadPoolExecutor(ExecutorUtils.commonPool(nThreads), maxPermits, 1000L, Integer.MAX_VALUE);
	}

}
