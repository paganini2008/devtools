package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.ThreadFactory;

/**
 * ThreadPoolBuilder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ThreadPoolBuilder {

	private int poolSize;
	private int queueSize;
	private long acquiredTimeout;
	private ThreadFactory threadFactory;

	public ThreadPoolBuilder(int nThreads) {
		this.poolSize = nThreads;
		this.queueSize = Integer.MAX_VALUE;
		this.acquiredTimeout = 1000L;
		this.threadFactory = new PooledThreadFactory();
	}

	public int getPoolSize() {
		return poolSize;
	}

	public ThreadPoolBuilder setPoolSize(int poolSize) {
		this.poolSize = poolSize;
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

	public long getAcquiredTimeout() {
		return acquiredTimeout;
	}

	public ThreadPoolBuilder setAcquiredTimeout(long acquiredTimeout) {
		this.acquiredTimeout = acquiredTimeout;
		return this;
	}

	public ThreadPool build() {
		return new GenericThreadPool(poolSize, Integer.MAX_VALUE, acquiredTimeout, queueSize, threadFactory);
	}

}
