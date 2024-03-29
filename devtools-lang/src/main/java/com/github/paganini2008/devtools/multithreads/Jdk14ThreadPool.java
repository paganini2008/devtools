/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * Jdk14ThreadPool
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class Jdk14ThreadPool implements ThreadPool {

	private static long threadSerialNo = 0;
	private final PoolManager poolManager;
	private final LinkedList<Runnable> waitQueue = new LinkedList<Runnable>();
	private final int maxQueueSize;
	private final Sync sync;
	private final long timeout;
	private volatile boolean running = true;
	private State state = new State();
	private Timer timer;
	private RejectedExecutionHandler rejectedExecutionHandler;

	public Jdk14ThreadPool(int maxPoolSize, long timeout, int queueSize) {
		this.poolManager = new PoolManager(maxPoolSize);
		this.sync = new Sync(maxPoolSize);
		this.timeout = timeout;
		this.maxQueueSize = queueSize;
	}

	public void keepIdleSize(int maxIdleSize, long checkInterval) {
		if (maxIdleSize < 1) {
			throw new IllegalArgumentException("MaxIdleSize must greater than 0.");
		}
		if (checkInterval >= 3) {
			this.timer = ThreadUtils.scheduleAtFixedRate(new IdleQueueKeeper(maxIdleSize), checkInterval, TimeUnit.SECONDS);
		}
	}

	public int getActiveThreadSize() {
		return poolManager.busyQueue.size();
	}

	public int getIdleThreadSize() {
		return poolManager.idleQueue.size();
	}

	public int getQueueSize() {
		return waitQueue.size();
	}

	public int getPoolSize() {
		return poolManager.poolSize;
	}

	public int getMaxPoolSize() {
		return poolManager.maxPoolSize;
	}

	public long getCompletedTaskCount() {
		return state.completedCount;
	}

	public long getFailedTaskCount() {
		return state.failedCount;
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	private static synchronized String getThreadName() {
		return "pool-thread-" + (++threadSerialNo);
	}

	static class State {

		volatile long failedCount = 0;
		volatile long completedCount = 0;
	}

	/**
	 * 
	 * IdleQueueKeeper
	 * 
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	class IdleQueueKeeper implements Executable {

		IdleQueueKeeper(int maxIdleSize) {
			this.maxIdleSize = maxIdleSize;
		}

		private int maxIdleSize;

		public boolean execute() {
			poolManager.retain(maxIdleSize);
			return isShutdown();
		}

	}

	/**
	 * 
	 * Reference
	 *
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	static class Reference<R> {

		R result;
		volatile boolean done;

		public R get() {
			return result;
		}

		public void set(R result) {
			this.result = result;
		}

		public boolean isDone() {
			return done;
		}

		public void setDone(boolean done) {
			this.done = done;
		}

	}

	/**
	 * 
	 * PromiseRunnable
	 *
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	static class PromiseRunnable<R> implements Runnable {

		final Map<Action<R>, R> results = new HashMap<Action<R>, R>();
		final Action<R> action;
		final Reference<R> reference;
		final ThreadPool threadPool;

		PromiseRunnable(Action<R> action, Reference<R> reference, ThreadPool threadPool) {
			this.action = action;
			this.reference = reference;
			this.threadPool = threadPool;
		}

		public void run() {
			R result = null;
			try {
				if (results.containsKey(action)) {
					R answer = results.remove(action);
					result = action.onReaction(answer, threadPool);
				} else {
					result = action.execute();
				}
			} catch (Exception e) {
				action.onFailure(e, threadPool);
			} finally {
				if (action.shouldReact(result)) {
					results.put(action, result);
					reference.set(result);
					threadPool.apply(this);
				} else {
					synchronized (reference) {
						reference.set(result);
						reference.notifyAll();
						reference.setDone(true);
					}
				}
			}
		}
	}

	static class DefaultPromise<R> implements Promise<R> {

		final Reference<R> reference;
		final long startTime;
		volatile boolean cancelled;
		volatile boolean done;

		DefaultPromise(Reference<R> reference) {
			this.reference = reference;
			this.startTime = System.currentTimeMillis();
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public boolean isDone() {
			return done || cancelled;
		}

		public long getElapsed() {
			return System.currentTimeMillis() - startTime;
		}

		public R get() {
			while (!isDone()) {
				synchronized (reference) {
					if (!reference.isDone()) {
						try {
							reference.wait();
						} catch (InterruptedException ignored) {
							break;
						}
					}
				}
				done = reference.isDone();
			}
			return reference.get();
		}

		public R get(long timeout) {
			if (!isDone()) {
				synchronized (reference) {
					if (!reference.isDone()) {
						try {
							reference.wait(timeout);
						} catch (InterruptedException ignored) {
						}
					}
				}
			}
			done = reference.isDone();
			return reference.get();
		}

		public void cancel() {
			if (!isDone()) {
				cancelled = true;
				synchronized (reference) {
					reference.notifyAll();
				}
			}
		}
	}

	public <R> Promise<R> submit(final Action<R> action) {
		final Reference<R> reference = new Reference<R>();
		apply(new PromiseRunnable<R>(action, reference, this));
		return new DefaultPromise<R>(reference);
	}

	public boolean apply(Runnable task) {
		if (!running) {
			throw new IllegalStateException("ThreadPool is shutdown now.");
		}
		boolean acquired = timeout > 0 ? sync.acquire(timeout) : timeout < 0 ? sync.tryAcquire() : sync.acquire();
		if (acquired) {
			WorkerThread workerThread = poolManager.borrow();
			if (workerThread != null) {
				workerThread.runTask(task);
				return true;
			} else {
				waitForNextExecuting(task);
			}
		} else {
			waitForNextExecuting(task);
		}
		return false;
	}

	private void waitForNextExecuting(Runnable task) {
		synchronized (waitQueue) {
			waitQueue.add(task);
			if (waitQueue.size() > maxQueueSize) {
				if (rejectedExecutionHandler != null) {
					rejectedExecutionHandler.handleRejectedExecution(task, this);
				} else {
					throw new IllegalStateException("WaitQueue Full!");
				}
			}
		}
	}

	protected void beforeRun(Thread thread, Runnable r) {
	}

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
	 * 
	 * 
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
				if (maxPermits - permits > 0) {
					permits++;
					return true;
				}
			}
			return false;
		}

		public boolean acquire() {
			while (true) {
				synchronized (lock) {
					if (maxPermits - permits > 0) {
						permits++;
						return true;
					} else {
						try {
							lock.wait(1000L);
						} catch (InterruptedException ignored) {
							break;
						}
					}
				}
			}
			return false;
		}

		public boolean acquire(long timeout) {
			final long begin = System.nanoTime();
			long elapsed;
			long m = timeout;
			long n = 0;
			while (true) {
				synchronized (lock) {
					if (maxPermits - permits > 0) {
						permits++;
						return true;
					} else {
						if (m > 0) {
							try {
								lock.wait(m, (int) n);
							} catch (InterruptedException ignored) {
								break;
							}
							elapsed = (System.nanoTime() - begin);
							m -= elapsed / 1000000L;
							n = elapsed % 1000000L;
						} else {
							break;
						}
					}
				}
			}
			return false;
		}

		public void release() {
			synchronized (lock) {
				permits--;
				lock.notifyAll();
			}
		}

		public void join() {
			while (permits > 0) {
				ThreadUtils.randomSleep(1000L);
			}
		}

	}

	/**
	 * 
	 * PoolManager
	 *
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	class PoolManager {

		final LinkedList<WorkerThread> idleQueue = new LinkedList<WorkerThread>();
		final LinkedList<WorkerThread> busyQueue = new LinkedList<WorkerThread>();
		final int maxPoolSize;
		volatile int poolSize = 0;

		PoolManager(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		synchronized void giveback(WorkerThread workerThread) {
			busyQueue.remove(workerThread);
			idleQueue.add(workerThread);
		}

		synchronized WorkerThread borrow() {
			WorkerThread workerThread = idleQueue.pollFirst();
			if (workerThread == null) {
				if (poolSize < maxPoolSize) {
					workerThread = new WorkerThread();
					poolSize++;
				}
			}
			if (workerThread != null) {
				busyQueue.add(workerThread);
			}
			return workerThread;
		}

		synchronized void destroy() {
			while (!busyQueue.isEmpty()) {
				ThreadUtils.randomSleep(1000L);
			}
			while (!idleQueue.isEmpty()) {
				destroy(idleQueue.pollFirst());
			}
		}

		synchronized void retain(int n) {
			int l = idleQueue.size();
			if (l > n) {
				for (int i = n; i < l; i++) {
					destroy(idleQueue.pollFirst());
				}
			}
		}

		synchronized void destroy(WorkerThread workerThread) {
			workerThread.destroy();
			poolSize--;
		}

	}

	/**
	 * 
	 * WorkerThread
	 *
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	class WorkerThread implements Runnable {

		final Object lock = new Object();
		final Thread thread;
		volatile boolean alive;
		volatile boolean idle;

		Runnable task;

		WorkerThread() {
			this.alive = true;
			this.idle = true;
			this.thread = newThread(this);
			if (thread.getState() == java.lang.Thread.State.NEW) {
				thread.start();
			}
		}

		boolean isAlive() {
			return alive;
		}

		boolean isIdle() {
			return idle;
		}

		private boolean runWhenIdle() {
			try {
				lock.wait(1000);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}

		private boolean runWhenBusy() {
			final Runnable r = task;
			Throwable cause = null;
			try {
				beforeRun(thread, r);
				r.run();
			} catch (Throwable e) {
				state.failedCount++;
				cause = e;
				return false;
			} finally {

				task = null;
				state.completedCount++;
				submitAgainIfPresent();
				idle = true;

				poolManager.giveback(this);
				sync.release();

				afterRun(r, cause);

			}
			return true;
		}

		public void run() {
			while (alive) {
				synchronized (lock) {
					if (idle) {
						runWhenIdle();
					} else {
						runWhenBusy();
					}
				}
			}
		}

		void runTask(Runnable task) {
			synchronized (lock) {
				if (alive) {
					if (idle) {
						this.task = task;
						idle = false;
						lock.notifyAll();
					} else {
						throw new IllegalStateException("Idle: " + idle);
					}
				}
			}
		}

		void destroy() {
			synchronized (lock) {
				while (!idle) {
					;
				}
				alive = false;
				if (idle) {
					lock.notifyAll();
				}
			}
		}

		String getName() {
			return thread.getName();
		}
	}

	protected Thread newThread(Runnable task) {
		return ThreadUtils.runAsThread(getThreadName(), task);
	}

	void submitAgainIfPresent() {
		Runnable task;
		synchronized (waitQueue) {
			task = waitQueue.pollFirst();
		}
		if (task != null) {
			apply(task);
		}

	}

	public synchronized void shutdown() {
		sync.join();
		running = false;
		if (timer != null) {
			timer.cancel();
		}
		synchronized (waitQueue) {
			while (!waitQueue.isEmpty()) {
				ThreadUtils.randomSleep(1000L);
			}
		}
		poolManager.destroy();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[SimpleThreadPool]: ").append("poolSize=").append(getPoolSize());
		str.append(", maxPoolSize=").append(getMaxPoolSize());
		str.append(", activeThreadSize=").append(getActiveThreadSize());
		str.append(", idleThreadSize=").append(getIdleThreadSize());
		str.append(", completedTaskCount=").append(getCompletedTaskCount());
		str.append(", queueSize=").append(getQueueSize());
		return str.toString();
	}

	public static void main3(String[] args) throws IOException {
		Jdk14ThreadPool threadPool = new Jdk14ThreadPool(10, 1000L, Integer.MAX_VALUE);
		Promise<Long> p = threadPool.submit(new Action<Long>() {

			public Long execute() throws Exception {
				ThreadUtils.sleep(10000L);
				return RandomUtils.randomLong(0, 10000);
			}

		});
		System.out.println("***: " + p.get(3000));

		threadPool.shutdown();
		System.out.println("SimpleThreadPool.main()");
	}
	
	public static void main(String[] args)throws IOException {
		test2();
	}

	public static void test1() throws IOException {
		Jdk14ThreadPool threadPool = new Jdk14ThreadPool(10, 1000L, Integer.MAX_VALUE);
		List<Promise<Long>> promises = new CopyOnWriteArrayList<Promise<Long>>();
		for (final int i : Sequence.forEach(0, 100)) {
			Promise<Long> p = threadPool.submit(new Action<Long>() {

				public Long execute() throws Exception {
					ThreadUtils.randomSleep(1000L);
					System.out.println(ThreadUtils.currentThreadName() + " say: " + i);
					return new Long(i);
				}

			});
			promises.add(p);
		}
		for (Promise<Long> p : promises) {
			System.out.println("***: " + p.get());
		}
		System.in.read();
		threadPool.shutdown();
		System.out.println("Jdk14ThreadPool.main()");
	}

	public static void test2() throws IOException {
		Jdk14ThreadPool threadPool = new Jdk14ThreadPool(10, 0L, Integer.MAX_VALUE);
		final AtomicInteger score = new AtomicInteger(0);
		for (final int i : Sequence.forEach(0, 500000)) {
			threadPool.apply(new Runnable() {
				public void run() {
					// ThreadUtils.randomSleep(2000L);
					System.out.println(Thread.currentThread().getName() + ": " + i + ", PoolSize: " + threadPool.getPoolSize()
							+ ", waitSize: " + threadPool.getQueueSize() + ", idleSize: " + threadPool.getIdleThreadSize());
					if (i % 3 == 0) {
						throw new IllegalStateException("Error!");
					}
					score.incrementAndGet();
				}
			});
		}
		System.out.println("Jdk14ThreadPool.main(): " + score);
		System.in.read();
		threadPool.shutdown();
		System.out.println("Jdk14ThreadPool.main()2: " + score);
	}
}
