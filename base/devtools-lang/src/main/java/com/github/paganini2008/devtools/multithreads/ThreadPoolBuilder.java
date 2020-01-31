package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.ThreadFactory;

import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * ThreadPoolBuilder
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2016-02
 */
public class ThreadPoolBuilder {

	private int maxPoolSize;
	private Latch latch;
	private int queueSize;
	private long timeout;
	private ThreadFactory threadFactory;

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public ThreadPoolBuilder setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

	public Latch getLatch() {
		return latch;
	}

	public ThreadPoolBuilder setLatch(Latch latch) {
		this.latch = latch;
		return this;
	}

	public ThreadPoolBuilder setMaxPermits(int maxPermits) {
		return setLatch(maxPermits > 0 ? new CounterLatch(maxPermits) : CounterLatch.newUnlimitedLatch());
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

	public long getTimeout() {
		return timeout;
	}

	public ThreadPoolBuilder setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	public ThreadPool build() {
		return new GenericThreadPool(maxPoolSize, latch, timeout, queueSize, threadFactory);
	}

	ThreadPoolBuilder() {
	}

	public static ThreadPoolBuilder common(int maxPoolSize) {
		ThreadPoolBuilder builder = new ThreadPoolBuilder();
		return builder.setMaxPoolSize(maxPoolSize).setLatch(CounterLatch.newUnlimitedLatch()).setQueueSize(Integer.MAX_VALUE)
				.setTimeout(-1L).setThreadFactory(new PooledThreadFactory());
	}

}
