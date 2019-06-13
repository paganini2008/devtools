package com.github.paganini2008.devtools.multithreads;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * JdkThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class JdkThreadPool extends ThreadPoolExecutor implements ThreadPool {

	protected JdkThreadPool(int poolSize, int maxPermits, long acquiredTimeout, int queueSize, ThreadFactory threadFactory) {
		super(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory,
				new ThreadPoolExecutor.AbortPolicy());
		this.latch = new CounterLatch(maxPermits);
		this.acquiredTimeout = acquiredTimeout;
		this.waitQueue = new LinkedBlockingQueue<Runnable>(queueSize);
	}

	private final Latch latch;
	private final long acquiredTimeout;
	private final Queue<Runnable> waitQueue;
	private RejectedExecutionHandler rejectedExecutionHandler;

	public boolean apply(Runnable r) {
		boolean acquired = acquiredTimeout > 0 ? latch.acquire(acquiredTimeout, TimeUnit.MILLISECONDS) : latch.acquire();
		if (acquired) {
			super.execute(r);
		} else {
			try {
				waitQueue.add(r);
			} catch (RuntimeException e) {
				if (rejectedExecutionHandler != null) {
					rejectedExecutionHandler.handleRejectedExecution(r, this);
				} else {
					throw new IllegalStateException("WaitQueue Full!");
				}
			}
		}
		return acquired;
	}

	public int getMaxPoolSize() {
		return getMaximumPoolSize();
	}

	public int getQueueSize() {
		return waitQueue.size();
	}

	public int getActiveThreadSize() {
		return getActiveCount();
	}

	public int getIdleThreadSize() {
		return getMaxPoolSize() - getActiveThreadSize();
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	public void execute(Runnable command) {
		apply(command);
	}

	protected final void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		latch.release();

		Runnable prev = waitQueue.poll();
		if (prev != null) {
			execute(prev);
		}
	}

	public void shutdown() {
		latch.join();
		super.shutdown();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[JdkThreadPool]: ").append("poolSize=").append(getPoolSize());
		str.append(", maxPoolSize=").append(getMaxPoolSize());
		str.append(", activeThreadSize=").append(getActiveThreadSize());
		str.append(", idleThreadSize=").append(getIdleThreadSize());
		str.append(", completedTaskCount=").append(getCompletedTaskCount());
		str.append(", queueSize=").append(getQueueSize());
		return str.toString();
	}

}
