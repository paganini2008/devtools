package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolBuilder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ThreadPoolBuilder {

	private String namePrefix;
	private int poolSize;
	private int maxPoolSize;
	private int queueSize;
	private ThreadFactory threadFactory;
	private RejectedExecutionHandler rejectedExecutionHandler;

	public ThreadPoolBuilder(int nThreads) {
		this(nThreads, nThreads);
	}

	public ThreadPoolBuilder(int nThreads, int maxThreads) {
		this.poolSize = nThreads;
		this.maxPoolSize = maxThreads;
		this.queueSize = -1;
		this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
	}

	public int getPoolSize() {
		return poolSize;
	}

	public ThreadPoolBuilder setPoolSize(int poolSize) {
		this.poolSize = poolSize;
		return this;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public ThreadPoolBuilder setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public ThreadPoolBuilder setQueueSize(int queueSize) {
		this.queueSize = queueSize;
		return this;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public ThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		return this;
	}

	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return rejectedExecutionHandler;
	}

	public ThreadPoolBuilder setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
		return this;
	}

	public ThreadPoolExecutor build() {
		BlockingQueue<Runnable> workQueue = queueSize == 0 ? new SynchronousQueue<Runnable>()
				: (queueSize < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queueSize));
		return new GracefulShutdownThreadPoolExecutor(poolSize, maxPoolSize, workQueue,
				threadFactory != null ? threadFactory : new PooledThreadFactory(namePrefix), rejectedExecutionHandler);
	}

	/**
	 * 
	 * GracefulShutdownThreadPoolExecutor
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class GracefulShutdownThreadPoolExecutor extends ThreadPoolExecutor {

		GracefulShutdownThreadPoolExecutor(int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue,
				ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, workQueue, threadFactory, handler);
		}

		private final Latch latch = new CounterLatch();

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			latch.acquire();
		}

		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			latch.release();
		}

		public void shutdown() {
			latch.join();
			super.shutdown();
		}

	}

}
