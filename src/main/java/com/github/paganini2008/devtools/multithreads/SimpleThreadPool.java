package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Vector;
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
	private final List<WorkerThread> idleQueue = new Vector<WorkerThread>();
	private final List<WorkerThread> busyQueue = new Vector<WorkerThread>();
	private final List<Runnable> workQueue = new Vector<Runnable>();
	private final int maxPoolSize;
	private final int maxQueueSize;
	private final Sync sync;
	private final long timeout;
	private LeaderThread leaderThread;
	private volatile int poolSize = 0;
	private volatile boolean running = true;
	private volatile long completedCount = 0;
	private Timer timer;
	private int maxIdleSize = 1;
	private RejectedExecutionHandler rejectedExecutionHandler;

	public SimpleThreadPool(int maxPoolSize, int maxPermits, long timeout, int queueSize) {
		this.maxPoolSize = maxPoolSize;
		this.sync = new Sync(maxPermits);
		this.timeout = timeout;
		this.maxQueueSize = queueSize;
		this.leaderThread = new LeaderThread();
	}

	public void keepIdleSize(int maxIdleSize, long checkInterval) {
		if (maxIdleSize < 1) {
			throw new IllegalArgumentException("MaxIdleSize must greater than 0.");
		}
		this.maxIdleSize = maxIdleSize;

		if (checkInterval >= 3) {
			this.timer = ThreadUtils.scheduleAtFixedRate(new IdleQueueKeeper(), checkInterval, TimeUnit.SECONDS);
		}
	}

	public void execute(Runnable command) {
		apply(command);
	}

	public int getActiveThreadSize() {
		return busyQueue.size();
	}

	public int getIdleThreadSize() {
		return idleQueue.size();
	}

	public int getQueueSize() {
		return workQueue.size();
	}

	public int getPoolSize() {
		return poolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public long getCompletedTaskCount() {
		return completedCount;
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	private static synchronized String getLeaderThreadName() {
		return "leader-pool-thread-" + (threadSerialNo++);
	}

	private static synchronized String getWorkerThreadName() {
		return "worker-pool-thread-" + (threadSerialNo++);
	}

	class LeaderThread implements Runnable {

		private final Object lock = new Object();
		private volatile boolean alive;
		private volatile boolean idle;
		private Thread thread;

		void active() {
			synchronized (lock) {
				if (alive) {
					if (idle) {
						idle = false;
						lock.notifyAll();
					}
				} else {
					alive = true;
					idle = false;
					this.thread = ThreadUtils.runAsThread(getLeaderThreadName(), this);
					this.thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
						public void uncaughtException(Thread t, Throwable e) {
							e.printStackTrace();
						}
					});
				}
			}
		}

		void destroy() {
			synchronized (lock) {
				while (!idle) {
					;
				}
				if (idle) {
					alive = false;
					lock.notifyAll();
				}
			}
		}

		public void run() {
			while (alive) {
				synchronized (lock) {
					if (idle) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
						}
					} else {
						boolean acquired = timeout > 0 ? sync.acquire(timeout) : sync.acquire();
						if (acquired) {
							WorkerThread workerThread = idleQueue.isEmpty() ? null : idleQueue.remove(0);
							if (workerThread == null) {
								if (poolSize < maxPoolSize) {
									workerThread = new WorkerThread();
									workerThread.task = workQueue.remove(0);
									workerThread.active();
									poolSize++;
								} else {
									idle = true;
									try {
										lock.wait();
									} catch (InterruptedException e) {
									}
								}
							} else {
								workerThread.task = workQueue.remove(0);
								workerThread.active();
							}
						} else {
							idle = true;
							try {
								lock.wait();
							} catch (InterruptedException e) {
							}
						}
						idle = workQueue.isEmpty();
					}
				}
			}
		}

	}

	/**
	 * 
	 * IdleQueueKeeper
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	class IdleQueueKeeper implements Executable {

		public boolean execute() {
			synchronized (SimpleThreadPool.this) {
				final int idleSize = idleQueue.size();
				if (idleSize > maxIdleSize) {
					List<WorkerThread> threads = new ArrayList<WorkerThread>(idleQueue.subList(0, idleSize - maxIdleSize));
					for (WorkerThread thread : threads) {
						thread.destroy();
					}
					idleQueue.removeAll(threads);
				}
			}
			return isShutdown();
		}

	}

	public synchronized boolean apply(Runnable task) {
		if (!running) {
			throw new IllegalStateException("ThreadPool is shutdown.");
		}
		boolean result = true;
		workQueue.add(task);
		if (workQueue.size() > maxQueueSize) {
			if (rejectedExecutionHandler != null) {
				rejectedExecutionHandler.handleRejectedExecution(task, this);
			} else {
				throw new IllegalStateException("WorkQueue Full!");
			}
			result = false;
		}
		if (result) {
			leaderThread.active();
		}
		return result;
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

		public boolean acquire() {
			while (true) {
				synchronized (lock) {
					if (permits < maxPermits) {
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
					if (permits < maxPermits) {
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
	 * WorkerThread
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	class WorkerThread implements Runnable {

		private final Object lock = new Object();
		private volatile boolean alive = false;
		private volatile boolean idle = false;
		private Thread thread;
		Runnable task;

		WorkerThread() {
		}

		boolean isAlive() {
			return alive;
		}

		boolean isIdle() {
			return idle;
		}

		private boolean runWhenIdle() {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}

		private boolean runWhenBusy() {
			busyQueue.add(this);
			final Runnable r = task;
			Throwable cause = null;
			try {
				beforeRun(thread, r);
				r.run();
			} catch (Throwable e) {
				cause = e;
				return false;
			} finally {
				completedCount++;
				busyQueue.remove(this);

				idle = true;
				idleQueue.add(this);
				afterRun(r, cause);
				sync.release();

				leaderThread.active();
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

		void active() {
			synchronized (lock) {
				if (alive) {
					if (idle) {
						idle = false;
						lock.notifyAll();
					}
				} else {
					alive = true;
					idle = false;
					this.thread = ThreadUtils.runAsThread(getWorkerThreadName(), this);
					this.thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
						public void uncaughtException(Thread t, Throwable e) {
							e.printStackTrace();
						}
					});
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
				poolSize--;
			}
		}
	}

	public synchronized void shutdown() {
		System.out.println("workQueue.size()： " + workQueue.size());
		leaderThread.destroy();

		while (!busyQueue.isEmpty()) {
			ThreadUtils.randomSleep(1000L);
		}
		for (int i = 0; i < idleQueue.size(); i++) {
			WorkerThread thread = idleQueue.get(i);
			thread.destroy();
		}
		idleQueue.clear();

		if (timer != null) {
			timer.cancel();
		}

		running = false;
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
		SimpleThreadPool threadPool = new SimpleThreadPool(10, 1000, 1000L, Integer.MAX_VALUE);
		final AtomicInteger score = new AtomicInteger(0);
		for (final int i : Sequence.forEach(0, 10000)) {
			threadPool.apply(new Runnable() {
				public void run() {
					// ThreadUtils.randomSleep(1000L);
					System.out.println(Thread.currentThread().getName() + ": " + i + ", PoolSize: " + threadPool.getPoolSize());
					if (i % 3 == 0) {
						throw new IllegalStateException("111111111111111111-->3");
					}
					score.incrementAndGet();
				}
			});
		}
		// System.in.read();
		// for (final int i : Sequence.forEach(0, 100000)) {
		// threadPool.apply(new Runnable() {
		// public void run() {
		// // ThreadUtils.randomSleep(1000L);
		// System.out.println(Thread.currentThread().getName() + ": " + i + ", PoolSize:
		// " + threadPool.getPoolSize());
		// if (i % 3 == 0) {
		// throw new IllegalStateException("111111111111111111-->3");
		// }
		// score.incrementAndGet();
		// }
		// });
		// }
		System.out.println("SimpleThreadPool.main(): " + score);
		System.in.read();
		threadPool.shutdown();
		System.out.println("SimpleThreadPool.main()2: " + score);
	}
}
