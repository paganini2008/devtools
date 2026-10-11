/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.multithreads;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * GenericThreadPool
 * <p>
 * A {@link ThreadPool} built on {@link ThreadPoolExecutor}. The number of concurrently running
 * tasks is limited by a {@link Latch}; a task that cannot get a permit (according to the
 * configured timeout) is parked in a bounded wait queue and executed as soon as a permit is
 * released.
 * <p>
 * Timeout semantics: {@code timeout > 0} waits up to that many milliseconds for a permit,
 * {@code timeout == 0} only tries once, and {@code timeout < 0} blocks until a permit is
 * available.
 * 
 * @author Fred Feng
 * @date 2019-06-13
 * @since 2.0.1
 */
public class GenericThreadPool extends ThreadPoolExecutor implements ThreadPool {

	public GenericThreadPool(int maxPoolSize, long timeout, int queueSize, ThreadFactory threadFactory) {
		this(maxPoolSize, new CounterLatch(maxPoolSize), timeout, queueSize, threadFactory);
	}

	/**
	 * Creates a thread pool.
	 * 
	 * @param maxPoolSize
	 *            the number of worker threads
	 * @param latch
	 *            the latch limiting concurrent tasks; if null, a {@link CounterLatch} with
	 *            {@code maxPoolSize} permits is used
	 * @param timeout
	 *            how long (milliseconds) to wait for a permit before parking the task in the wait
	 *            queue
	 * @param queueSize
	 *            capacity of the wait queue; a value &lt;= 0 means tasks are never parked
	 * @param threadFactory
	 *            the thread factory; if null, a {@link PooledThreadFactory} is used
	 */
	public GenericThreadPool(int maxPoolSize, Latch latch, long timeout, int queueSize, ThreadFactory threadFactory) {
		super(maxPoolSize, maxPoolSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				threadFactory != null ? threadFactory : new PooledThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
		this.latch = latch != null ? latch : new CounterLatch(maxPoolSize);
		this.timeout = timeout;
		this.waitQueue = queueSize > 0 ? new LinkedBlockingQueue<Runnable>(queueSize) : new SynchronousQueue<Runnable>();
	}

	private final Latch latch;
	private final long timeout;
	private final Queue<Runnable> waitQueue;
	private final AtomicInteger failedCount = new AtomicInteger(0);
	private volatile RejectedExecutionHandler rejectedExecutionHandler;
	private final ThreadLocal<Boolean> inWorker = new ThreadLocal<Boolean>();

	/**
	 * Executes the task if a permit can be obtained, otherwise parks it in the wait queue.
	 * 
	 * @param task
	 *            the task to run
	 * @return true if the task was handed to a worker immediately, false if it was parked (or
	 *         rejected by the {@link RejectedExecutionHandler})
	 * @throws IllegalStateException
	 *             if the wait queue is full and no {@link RejectedExecutionHandler} is set
	 */
	public boolean apply(Runnable task) {
		if (task == null) {
			throw new NullPointerException("Null task");
		}
		boolean acquired = timeout > 0 ? latch.acquire(timeout, TimeUnit.MILLISECONDS)
				: timeout == 0 ? latch.tryAcquire() : latch.acquire();
		if (acquired) {
			return doExecute(task);
		}
		if (!waitQueue.offer(task)) {
			reject(task, "WaitQueue Full!");
			return false;
		}
		// A permit may have been released while the task was being enqueued
		drainWaitQueue();
		return false;
	}

	private boolean doExecute(Runnable task) {
		try {
			super.execute(task);
			return true;
		} catch (RejectedExecutionException e) {
			latch.release();
			reject(task, "ThreadPool is shutdown.");
			return false;
		}
	}

	private void reject(Runnable task, String msg) {
		RejectedExecutionHandler handler = rejectedExecutionHandler;
		if (handler != null) {
			handler.handleRejectedExecution(task, this);
		} else {
			throw new IllegalStateException(msg);
		}
	}

	private void drainWaitQueue() {
		while (!waitQueue.isEmpty() && latch.tryAcquire()) {
			Runnable task = waitQueue.poll();
			if (task == null) {
				latch.release();
				break;
			}
			doExecute(task);
		}
	}

	/**
	 * Submits an {@link Action}; the action and its chain of reactions run in a single worker.
	 * 
	 * @param action
	 *            the action to run
	 * @return a promise of the last non-null result of the action
	 */
	public <R> Promise<R> submit(final Action<R> action) {
		ActionPromise<R> promise = new ActionPromise<R>(action, this);
		apply(promise);
		return promise;
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

	public long getFailedTaskCount() {
		return failedCount.get();
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	public void execute(Runnable command) {
		apply(command);
	}

	/**
	 * Marks the current thread as running a task of this pool. Subclasses overriding this method
	 * must call {@code super.beforeExecute(thread, runnable)}.
	 */
	@Override
	protected void beforeExecute(Thread thread, Runnable runnable) {
		inWorker.set(Boolean.TRUE);
		super.beforeExecute(thread, runnable);
	}

	protected final void afterExecute(Runnable runnable, Throwable e) {
		inWorker.remove();
		super.afterExecute(runnable, e);
		latch.release();
		if (e != null) {
			failedCount.incrementAndGet();
		}
		drainWaitQueue();
	}

	/**
	 * Waits until all running and parked tasks have completed, then shuts down the pool. When
	 * called from a task of this pool, waiting would deadlock (the calling task holds a permit), so
	 * the shutdown is carried out asynchronously by a background thread and this method returns
	 * immediately.
	 */
	public void shutdown() {
		if (inWorker.get() != null) {
			Thread t = new Thread(this::shutdown, "GenericThreadPool-shutdown");
			t.setDaemon(true);
			t.start();
			return;
		}
		while (true) {
			latch.join();
			if (waitQueue.isEmpty() && !latch.isLocked()) {
				break;
			}
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			drainWaitQueue();
			if (!waitQueue.isEmpty()) {
				Thread.yield();
			}
		}
		super.shutdown();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[GenericThreadPool]: ");
		str.append("maxPoolSize=").append(getMaxPoolSize());
		str.append(", activeThreadSize=").append(getActiveThreadSize());
		str.append(", idleThreadSize=").append(getIdleThreadSize());
		str.append(", completedTaskCount=").append(getCompletedTaskCount());
		str.append(", failedTaskCount=").append(getFailedTaskCount());
		str.append(", queueSize=").append(getQueueSize());
		return str.toString();
	}

}
