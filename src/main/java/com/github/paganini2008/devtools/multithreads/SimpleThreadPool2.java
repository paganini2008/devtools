package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.paganini2008.devtools.Sequence;

public class SimpleThreadPool2 implements Executor {

	private final Queue<Caller> idleQueue = new LinkedBlockingQueue<Caller>();
	private final Queue<Caller> activeQueue = new LinkedBlockingQueue<Caller>();
	private final Queue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private final Queue<Runnable> waitQueue;
	private final AtomicInteger poolSize = new AtomicInteger(0);
	private final Semaphore limitation;
	private final long timeout;
	private final int maxPoolSize;
	private final Sync sync;
	private ThreadFactory threadFactory = new PooledThreadFactory();

	public SimpleThreadPool2(int initialSize, int maxPoolSize, int queueSize, long timeout) {
		this.maxPoolSize = maxPoolSize;
		this.timeout = timeout;
		this.limitation = new Semaphore(maxPoolSize);
		this.waitQueue = new LinkedBlockingQueue<Runnable>(queueSize);
		this.sync = new Sync(10);
		if (initialSize > 0 && initialSize <= maxPoolSize) {
			for (int i = 0; i < initialSize; i++) {
				if (needToCreate()) {
					idleQueue.offer(new Caller(sync.getLock()));
				}
			}
			System.out.println("SimpleThreadPool.SimpleThreadPool()");
		}
	}

	static class Sync {

		final AtomicPositiveInteger counter = new AtomicPositiveInteger(0);
		final List<Lock> locks = new CopyOnWriteArrayList<Lock>();
		final int size;

		Sync(int size) {
			for (int i = 0; i < size; i++) {
				locks.add(new ReentrantLock());
			}
			this.size = size;
		}

		Lock getLock() {
			return locks.get(counter.getAndIncrement() % size);
		}

	}

	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	class Caller implements Runnable {

		final AtomicBoolean alive = new AtomicBoolean(false);
		final AtomicBoolean idle = new AtomicBoolean(false);
		final Lock lock;
		final Condition condition;
		Thread thread;

		Caller(Lock lock) {
			this.lock = lock;
			this.condition = lock.newCondition();
		}

		private boolean runWhenIdle() {
			try {
				condition.await();
			} catch (InterruptedException ignored) {
				thread.getUncaughtExceptionHandler().uncaughtException(thread, ignored);
				idleQueue.remove(this);
				return false;
			}
			return true;
		}

		private boolean runWhenActive() {
			activeQueue.offer(this);
			Runnable r = workQueue.poll();
			try {
				r.run();
			} catch (Throwable e) {
				thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
				return false;
			} finally {
				activeQueue.remove(this);
				idleQueue.offer(this);
				idle.set(true);

				limitation.release();
				executeAgain();
			}
			return true;
		}

		public void run() {
			while (alive.get()) {
				lock.lock();
				boolean destroy = true;
				try {
					if (idle.get()) {
						destroy = runWhenIdle();
					} else {
						destroy = runWhenActive();
					}
				} finally {
					lock.unlock();
				}
				if (!destroy) {
					break;
				}
			}
		}

		String getThreadName() {
			return thread.getName();
		}

		boolean isIdle() {
			return idle.get();
		}

		void active() {
			lock.lock();
			if (alive.get()) {
				if (idle.get()) {
					idle.set(false);
					condition.signal();
				}
			} else {
				alive.set(true);
				idle.set(false);
				this.thread = ThreadUtils.runAsThread(threadFactory, this);
				this.thread.setUncaughtExceptionHandler((t, e) -> {
					e.printStackTrace();
				});
			}
			lock.unlock();
		}

		void destroy() {
			lock.lock();
			while (!idle.get()) {
				;
			}
			alive.set(false);
			if (idle.get()) {
				thread.interrupt();
			}
			poolSize.decrementAndGet();
			System.out.println("销毁线程： " + thread.getName() + "， 当前数量：" + poolSize.get());
			lock.unlock();
		}

		public String toString() {
			return thread.getName();
		}

	}

	private boolean needToCreate() {
		if (poolSize.get() == maxPoolSize) {
			return false;
		}
		return poolSize.getAndIncrement() < maxPoolSize;
	}

	private void executeAgain() {
		Runnable r = waitQueue.poll();
		if (r != null) {
			execute(r);
		}
	}

	public void execute(Runnable r) {
		boolean acquired = false;
		try {
			if (limitation.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
				acquired = true;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (acquired) {
			workQueue.offer(r);
			Caller caller = idleQueue.poll();
			if (caller == null) {
				if (needToCreate()) {
					caller = new Caller(sync.getLock());
					caller.active();
				}
			} else {
				caller.active();
			}
		} else {
			try {
				waitQueue.add(r);
				System.out.println("waitQueue.size(): " + waitQueue.size());
			} catch (RuntimeException e) {
				workQueue.remove(r);
				onRejection(r);
			}
		}
	}

	protected void onRejection(Runnable r) {
		System.out.println("拒绝： " + r);
	}

	public void shutdown() {
		System.out.println("limitation.availablePermits(): " + limitation.availablePermits());
		while (limitation.availablePermits() != maxPoolSize) {
			System.out.println("KK: " + limitation.availablePermits());
		}
		System.out.println("waitQueue: " + waitQueue);
		System.out.println("workQueue: " + workQueue);
		System.out.println("idleQueue: " + idleQueue);
		System.out.println("activeQueue: " + activeQueue);
		while (!idleQueue.isEmpty()) {
			Caller caller = idleQueue.poll();
			caller.destroy();
		}
	}

	public static void main(String[] args) throws IOException {
		SimpleThreadPool2 threadPool = new SimpleThreadPool2(0, 10, Integer.MAX_VALUE, 1000);
		final AtomicInteger score = new AtomicInteger(0);
		for (final int i : Sequence.forEach(0, 1000)) {
			threadPool.execute(new Runnable() {
				public void run() {
					// ThreadUtils.randomSleep(1000L);
					System.out.println(Thread.currentThread().getName() + ": " + i);
					if (i % 3 == 0) {
						// throw new IllegalStateException("111111111111111111-->3");
					}
					score.incrementAndGet();
				}
			});
		}
		System.in.read();
		System.out.println("SimpleThreadPool.main(): " + score);
		threadPool.shutdown();
		System.out.println("SimpleThreadPool.main()2: " + score);
	}

}
