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

import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * Jdk14ThreadPool
 * <p>
 * A self-contained {@link ThreadPool} implementation that manages its own worker threads without
 * relying on {@code java.util.concurrent} executors. At most {@code maxPoolSize} tasks run at the
 * same time; a task that cannot get a worker (according to the configured timeout) is parked in a
 * bounded wait queue and run as soon as a worker becomes free.
 * <p>
 * Timeout semantics: {@code timeout > 0} waits up to that many milliseconds for a free worker,
 * {@code timeout < 0} only tries once, and {@code timeout == 0} blocks until a worker is free
 * (note: this differs from {@link GenericThreadPool}).
 * 
 * @author Fred Feng
 * @date 2019-06-06
 * @since 2.0.1
 */
public class Jdk14ThreadPool implements ThreadPool {

	private static final AtomicLong threadSerialNo = new AtomicLong(0);
	private final PoolManager poolManager;
	private final LinkedList<Runnable> waitQueue = new LinkedList<Runnable>();
	private final int maxQueueSize;
	private final Sync sync;
	private final long timeout;
	private volatile boolean running = true;
	private final AtomicLong completedCount = new AtomicLong(0);
	private final AtomicLong failedCount = new AtomicLong(0);
	private volatile Timer timer;
	private volatile RejectedExecutionHandler rejectedExecutionHandler;
	private final ThreadLocal<Boolean> inWorker = new ThreadLocal<Boolean>();

	/**
	 * Creates a thread pool.
	 * 
	 * @param maxPoolSize
	 *            the maximum number of worker threads, must be greater than 0
	 * @param timeout
	 *            how long (milliseconds) to wait for a free worker before parking the task
	 * @param queueSize
	 *            capacity of the wait queue
	 */
	public Jdk14ThreadPool(int maxPoolSize, long timeout, int queueSize) {
		if (maxPoolSize < 1) {
			throw new IllegalArgumentException("MaxPoolSize must be greater than 0.");
		}
		this.poolManager = new PoolManager(maxPoolSize);
		this.sync = new Sync(maxPoolSize);
		this.timeout = timeout;
		this.maxQueueSize = queueSize;
	}

	/**
	 * Periodically destroys idle worker threads beyond {@code maxIdleSize}. Intervals shorter
	 * than 3 seconds are ignored (no check is scheduled).
	 * 
	 * @param maxIdleSize
	 *            the number of idle workers to keep, must be greater than 0
	 * @param checkInterval
	 *            check interval in seconds
	 */
	public synchronized void keepIdleSize(int maxIdleSize, long checkInterval) {
		if (maxIdleSize < 1) {
			throw new IllegalArgumentException("MaxIdleSize must greater than 0.");
		}
		if (checkInterval >= 3 && running) {
			if (timer != null) {
				timer.cancel();
			}
			this.timer = ThreadUtils.scheduleAtFixedRate(new IdleQueueKeeper(maxIdleSize), checkInterval, TimeUnit.SECONDS);
		}
	}

	public int getActiveThreadSize() {
		return poolManager.getBusySize();
	}

	public int getIdleThreadSize() {
		return poolManager.getIdleSize();
	}

	public int getQueueSize() {
		synchronized (waitQueue) {
			return waitQueue.size();
		}
	}

	public int getPoolSize() {
		return poolManager.getPoolSize();
	}

	public int getMaxPoolSize() {
		return poolManager.maxPoolSize;
	}

	public long getCompletedTaskCount() {
		return completedCount.get();
	}

	public long getFailedTaskCount() {
		return failedCount.get();
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	private static String getThreadName() {
		return "pool-thread-" + threadSerialNo.incrementAndGet();
	}

	/**
	 * 
	 * IdleQueueKeeper
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	class IdleQueueKeeper implements Executable {

		IdleQueueKeeper(int maxIdleSize) {
			this.maxIdleSize = maxIdleSize;
		}

		private final int maxIdleSize;

		public boolean execute() {
			if (isShutdown()) {
				return false;
			}
			poolManager.retain(maxIdleSize);
			return true;
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

	/**
	 * Runs the task on a free worker, or parks it in the wait queue.
	 * 
	 * @param task
	 *            the task to run
	 * @return true if the task was handed to a worker immediately, false if it was parked (or
	 *         rejected by the {@link RejectedExecutionHandler})
	 * @throws IllegalStateException
	 *             if the pool is shut down, or the wait queue is full and no
	 *             {@link RejectedExecutionHandler} is set
	 */
	public boolean apply(Runnable task) {
		if (task == null) {
			throw new NullPointerException("Null task");
		}
		if (!running) {
			throw new IllegalStateException("ThreadPool is shutdown now.");
		}
		boolean acquired = timeout > 0 ? sync.acquire(timeout) : timeout < 0 ? sync.tryAcquire() : sync.acquire();
		if (acquired) {
			dispatch(task);
			return true;
		}
		boolean queued;
		synchronized (waitQueue) {
			queued = waitQueue.size() < maxQueueSize && waitQueue.add(task);
		}
		if (!queued) {
			RejectedExecutionHandler handler = rejectedExecutionHandler;
			if (handler != null) {
				handler.handleRejectedExecution(task, this);
				return false;
			}
			throw new IllegalStateException("WaitQueue Full!");
		}
		// A worker may have been freed while the task was being enqueued
		drainWaitQueue();
		return false;
	}

	/**
	 * Hands the task to a worker; the caller must hold a permit.
	 */
	private void dispatch(Runnable task) {
		WorkerThread workerThread = poolManager.borrow();
		workerThread.runTask(task);
	}

	private void drainWaitQueue() {
		while (true) {
			synchronized (waitQueue) {
				if (waitQueue.isEmpty()) {
					return;
				}
			}
			if (!sync.tryAcquire()) {
				return;
			}
			Runnable task;
			synchronized (waitQueue) {
				task = waitQueue.pollFirst();
			}
			if (task == null) {
				sync.release();
				return;
			}
			dispatch(task);
		}
	}

	/**
	 * Callback invoked in the worker thread before a task runs.
	 * 
	 * @param thread
	 *            the worker thread
	 * @param r
	 *            the task
	 */
	protected void beforeRun(Thread thread, Runnable r) {
	}

	/**
	 * Callback invoked in the worker thread after a task has run.
	 * 
	 * @param r
	 *            the task
	 * @param e
	 *            the exception thrown by the task, or null if it completed normally
	 */
	protected void afterRun(Runnable r, Throwable e) {
	}

	public boolean isShutdown() {
		return !running;
	}

	/**
	 * 
	 * Sync
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class Sync {

		final Object lock = new Object();
		final int maxPermits;
		int permits = 0;

		Sync(int maxPermits) {
			this.maxPermits = maxPermits;
		}

		public int availablePermits() {
			synchronized (lock) {
				return maxPermits - permits;
			}
		}

		public boolean tryAcquire() {
			synchronized (lock) {
				if (permits < maxPermits) {
					permits++;
					return true;
				}
			}
			return false;
		}

		public boolean acquire() {
			synchronized (lock) {
				while (permits >= maxPermits) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return false;
					}
				}
				permits++;
				return true;
			}
		}

		public boolean acquire(long timeout) {
			long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
			final long deadline = System.nanoTime() + nanos;
			synchronized (lock) {
				while (permits >= maxPermits) {
					if (nanos <= 0) {
						return false;
					}
					try {
						TimeUnit.NANOSECONDS.timedWait(lock, nanos);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return false;
					}
					nanos = deadline - System.nanoTime();
				}
				permits++;
				return true;
			}
		}

		public void release() {
			synchronized (lock) {
				if (permits > 0) {
					permits--;
				}
				lock.notifyAll();
			}
		}

		public boolean join() {
			synchronized (lock) {
				while (permits > 0) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return false;
					}
				}
				return true;
			}
		}

	}

	/**
	 * 
	 * PoolManager
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	class PoolManager {

		final LinkedList<WorkerThread> idleQueue = new LinkedList<WorkerThread>();
		final LinkedList<WorkerThread> busyQueue = new LinkedList<WorkerThread>();
		final int maxPoolSize;
		int poolSize = 0;
		boolean closed;

		PoolManager(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		synchronized int getPoolSize() {
			return poolSize;
		}

		synchronized int getIdleSize() {
			return idleQueue.size();
		}

		synchronized int getBusySize() {
			return busyQueue.size();
		}

		synchronized void giveback(WorkerThread workerThread) {
			busyQueue.remove(workerThread);
			if (closed) {
				// The pool has been shut down: a worker that finishes a late task must not linger
				destroy(workerThread);
			} else {
				idleQueue.addFirst(workerThread);
			}
		}

		/**
		 * Borrows an idle worker or creates a new one. The caller must hold a permit, which
		 * guarantees that a worker is always available.
		 */
		synchronized WorkerThread borrow() {
			WorkerThread workerThread = idleQueue.pollFirst();
			if (workerThread == null) {
				if (poolSize >= maxPoolSize) {
					throw new IllegalStateException("No worker available");
				}
				workerThread = new WorkerThread();
				poolSize++;
			}
			busyQueue.add(workerThread);
			return workerThread;
		}

		synchronized void destroyAll() {
			closed = true;
			while (!idleQueue.isEmpty()) {
				destroy(idleQueue.pollFirst());
			}
		}

		synchronized void retain(int n) {
			while (idleQueue.size() > n) {
				destroy(idleQueue.pollLast());
			}
		}

		private void destroy(WorkerThread workerThread) {
			workerThread.destroy();
			poolSize--;
		}

	}

	/**
	 * 
	 * WorkerThread
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	class WorkerThread implements Runnable {

		final Object lock = new Object();
		final Thread thread;
		boolean alive = true;
		Runnable task;

		WorkerThread() {
			this.thread = newThread(this);
			if (thread.getState() == Thread.State.NEW) {
				thread.start();
			}
		}

		public void run() {
			inWorker.set(Boolean.TRUE);
			while (true) {
				Runnable r;
				synchronized (lock) {
					while (alive && task == null) {
						try {
							lock.wait();
						} catch (InterruptedException ignored) {
							// A worker only stops when destroyed
						}
					}
					if (task == null) {
						break;
					}
					r = task;
				}
				runTask0(r);
			}
		}

		private void runTask0(Runnable r) {
			Throwable cause = null;
			// Do not let an interrupt status set by a previous task leak into this one
			Thread.interrupted();
			try {
				beforeRun(thread, r);
				r.run();
			} catch (Throwable e) {
				cause = e;
				failedCount.incrementAndGet();
			} finally {
				completedCount.incrementAndGet();
				synchronized (lock) {
					task = null;
				}
				poolManager.giveback(this);
				sync.release();
				try {
					afterRun(r, cause);
				} catch (Throwable ignored) {
				}
				drainWaitQueue();
			}
		}

		void runTask(Runnable r) {
			synchronized (lock) {
				if (!alive || task != null) {
					throw new IllegalStateException("Worker is not available");
				}
				task = r;
				lock.notifyAll();
			}
		}

		void destroy() {
			synchronized (lock) {
				alive = false;
				lock.notifyAll();
			}
		}

		String getName() {
			return thread.getName();
		}
	}

	/**
	 * Creates and starts the thread backing a worker. Subclasses may override it to customize
	 * the thread; a thread returned in the NEW state is started by the caller.
	 * 
	 * @param task
	 *            the worker loop
	 * @return the worker thread
	 */
	protected Thread newThread(Runnable task) {
		return ThreadUtils.runAsThread(getThreadName(), task);
	}

	/**
	 * Stops accepting new tasks, waits until all running and parked tasks have completed and
	 * then terminates all worker threads. If the calling thread is interrupted while waiting,
	 * it returns early with the interrupt status set; calling it again completes the shutdown.
	 * When called from a task of this pool, waiting would deadlock (the calling task occupies a
	 * worker), so new tasks are rejected immediately but waiting and terminating the workers is
	 * carried out asynchronously by a background thread.
	 */
	public void shutdown() {
		synchronized (this) {
			running = false;
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		}
		if (inWorker.get() != null) {
			Thread t = new Thread(this::awaitAndDestroy, "Jdk14ThreadPool-shutdown");
			t.setDaemon(true);
			t.start();
			return;
		}
		awaitAndDestroy();
	}

	private void awaitAndDestroy() {
		while (true) {
			if (!sync.join()) {
				break;
			}
			boolean empty;
			synchronized (waitQueue) {
				empty = waitQueue.isEmpty();
			}
			if (empty && sync.availablePermits() == sync.maxPermits) {
				break;
			}
			drainWaitQueue();
		}
		poolManager.destroyAll();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[Jdk14ThreadPool]: ").append("poolSize=").append(getPoolSize());
		str.append(", maxPoolSize=").append(getMaxPoolSize());
		str.append(", activeThreadSize=").append(getActiveThreadSize());
		str.append(", idleThreadSize=").append(getIdleThreadSize());
		str.append(", completedTaskCount=").append(getCompletedTaskCount());
		str.append(", queueSize=").append(getQueueSize());
		return str.toString();
	}

}
