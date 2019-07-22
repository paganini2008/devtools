package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * SimpleThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2012-01
 * @version 1.0
 */
public class SimpleThreadPool implements ThreadPool {

	private static long threadSerialNo = 0;

	private final PoolManager poolManager;
	private final LinkedList<Runnable> waitQueue = new LinkedList<Runnable>();
	private final int maxQueueSize;
	private final Sync sync;
	private final long timeout;
	private volatile boolean running = true;
	private volatile long failedCount = 0;
	private volatile long completedCount = 0;
	private Timer timer;
	private RejectedExecutionHandler rejectedExecutionHandler;

	public SimpleThreadPool(int maxPoolSize, long timeout, int queueSize) {
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

	public void execute(Runnable command) {
		apply(command);
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
		return completedCount;
	}

	public long getFailedTaskCount() {
		return failedCount;
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	private static synchronized String getThreadName() {
		return "pool-thread-" + (++threadSerialNo);
	}

	/**
	 * 
	 * IdleQueueKeeper
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @created 2019-05
	 * @version 1.0
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

	public boolean apply(Runnable runnable) {
		if (!running) {
			throw new IllegalStateException("ThreadPool is shutdown now.");
		}
		boolean acquired = timeout > 0 ? sync.acquire(timeout) : timeout < 0 ? sync.tryAcquire() : sync.acquire();
		if (acquired) {
			WorkerThread workerThread = poolManager.borrow();
			if (workerThread != null) {
				workerThread.runTask(runnable);
				return true;
			} else {
				waitForNextExecuting(runnable);
			}
		} else {
			waitForNextExecuting(runnable);
		}
		return false;
	}

	private void waitForNextExecuting(Runnable runnable) {
		synchronized (waitQueue) {
			waitQueue.add(runnable);
			if (waitQueue.size() > maxQueueSize) {
				if (rejectedExecutionHandler != null) {
					rejectedExecutionHandler.handleRejectedExecution(runnable, this);
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
	 * @revised 2019-05
	 * @created 2012-01
	 * @version 1.0
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
							lock.wait();
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

	}

	/**
	 * 
	 * PoolManager
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2012-01
	 * @version 1.0
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
	 * @created 2019-07
	 * @created 2012-01
	 * @version 1.0
	 */
	class WorkerThread implements Runnable {

		private final Object lock = new Object();
		private final Thread thread;
		private volatile boolean alive;
		private volatile boolean idle;

		Runnable task;

		WorkerThread() {
			this.alive = true;
			this.idle = true;
			this.thread = ThreadUtils.runAsThread(getThreadName(), this);
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
				failedCount++;
				cause = e;
				return false;
			} finally {

				task = null;
				completedCount++;
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

	void submitAgainIfPresent() {

		Runnable runnable;
		synchronized (waitQueue) {
			runnable = waitQueue.pollFirst();
		}
		if (runnable != null) {
			apply(runnable);
		}

	}

	public synchronized void shutdown() {
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

	public static void main(String[] args) throws IOException {
		GenericThreadPool threadPool = new GenericThreadPool(10, 100, 0L, Integer.MAX_VALUE, new PooledThreadFactory());
		final AtomicInteger score = new AtomicInteger(0);
		for (final int i : Sequence.forEach(0, 100000)) {
			threadPool.apply(new Runnable() {
				public void run() {
					// ThreadUtils.randomSleep(2000L);
					System.out.println(Thread.currentThread().getName() + ": " + i + ", PoolSize: " + threadPool.getPoolSize()
							+ ", waitSize: " + threadPool.getQueueSize() + ", idleSize: " + threadPool.getIdleThreadSize());
					if (i % 3 == 0) {
						//throw new IllegalStateException("111111111111111111-->3");
					}
					score.incrementAndGet();
				}
			});
		}
		System.out.println("SimpleThreadPool.main(): " + score);
		System.in.read();
		threadPool.shutdown();
		System.out.println("SimpleThreadPool.main()2: " + score);
	}
}
