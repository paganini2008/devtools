package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * JdkExecutorThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2013-06
 * @version 1.0
 */
public class JdkExecutorThreadPool implements ThreadPool, java.util.concurrent.RejectedExecutionHandler {

	private final ThreadPoolExecutor threads;
	private final Latch latch;
	private final long timeout;
	private final Queue<Runnable> waitQueue;
	private RejectedExecutionHandler rejectedExecutionHandler;

	public JdkExecutorThreadPool(int maxPoolSize, long timeout, int queueSize) {
		this.latch = new RecursiveLatch(maxPoolSize);
		this.threads = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				new PooledThreadFactory(), this) {

			protected void beforeExecute(Thread t, Runnable r) {
				super.beforeExecute(t, r);
				beforeRun(t, r);
			}

			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				latch.release();

				Runnable prev = waitQueue.poll();
				if (prev != null) {
					apply(prev);
				}

				afterRun(r, t);
			}
		};
		this.timeout = timeout;
		this.waitQueue = new LinkedBlockingQueue<Runnable>(queueSize);
	}

	public boolean apply(Runnable r) {
		boolean acquired = timeout > 0 ? latch.acquire(timeout, TimeUnit.MILLISECONDS) : latch.acquire();
		if (acquired) {
			threads.execute(r);
			return true;
		} else {
			try {
				waitQueue.add(r);
			} catch (RuntimeException e) {
				rejectedExecution(r, threads);
			}
		}
		return false;
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	protected void beforeRun(Thread t, Runnable r) {
	}

	protected void afterRun(Runnable r, Throwable e) {
	}

	public int getPoolSize() {
		return threads.getPoolSize();
	}

	public int getQueueSize() {
		return waitQueue.size();
	}

	public int getIdleThreadSize() {
		return threads.getMaximumPoolSize() - threads.getActiveCount();
	}

	public int getActiveThreadSize() {
		return threads.getActiveCount();
	}

	public long getCompletedTaskCount() {
		return threads.getCompletedTaskCount();
	}

	public boolean isShutdown() {
		return threads.isShutdown();
	}

	public int getMaxPoolSize() {
		return threads.getMaximumPoolSize();
	}

	public void shutdown() {
		while (latch.isLocked() || !waitQueue.isEmpty()) {
			ThreadUtils.randomSleep(1000L);
		}
		ExecutorUtils.gracefulShutdown(threads, 60000);
	}

	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		if (rejectedExecutionHandler != null) {
			rejectedExecutionHandler.handleRejectedExecution(r, this);
		} else {
			throw new IllegalStateException("Queue Full!");
		}
	}

	public String toString() {
		return threads.toString();
	}

	public static void main(String[] args) throws IOException {
		final AtomicInteger score = new AtomicInteger(0);
		JdkExecutorThreadPool threadPool = new JdkExecutorThreadPool(10, 0L, 10);
		for (int i : Sequence.forEach(0, 10000)) {
			threadPool.apply(() -> {
				// ThreadUtils.randomSleep(1000L);
				System.out.println(i + ", " + threadPool.getActiveThreadSize());
				score.incrementAndGet();
			});
		}
		// System.in.read();
		threadPool.shutdown();
		System.out.println("score: " + score);
	}

}
