package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Latchs
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public abstract class Latchs {

	public static class AtomicSyncLatch implements Latch {

		private final AtomicSync sync;
		private final int maxPermits;

		AtomicSyncLatch(int maxPermits) {
			this.sync = new AtomicSync();
			this.maxPermits = maxPermits;
		}

		public long getPermits() {
			return sync.get();
		}

		public boolean acquire() {
			return sync.countUp();
		}

		public boolean acquire(long timeout, TimeUnit timeUnit) {
			return sync.countUp(maxPermits, timeout, timeUnit);
		}

		public void release() {
			sync.countDown();
		}

		public void join() {
			sync.join();
		}

	}

	public static class SemaphoreLatch implements Latch {

		private final Semaphore latch;
		private final int maxPermits;

		SemaphoreLatch(int maxPermits) {
			this.latch = new Semaphore(maxPermits);
			this.maxPermits = maxPermits;
		}

		public long getPermits() {
			return maxPermits - latch.availablePermits();
		}

		public boolean acquire() {
			boolean result = true;
			try {
				latch.acquire();
			} catch (InterruptedException e) {
				result = false;
			}
			return result;
		}

		public boolean acquire(long timeout, TimeUnit timeUnit) {
			boolean result = true;
			try {
				result = latch.tryAcquire(timeout, timeUnit);
			} catch (InterruptedException e) {
				result = false;
			}
			return result;
		}

		public void release() {
			latch.release();
		}

		public void join() {
			while (latch.availablePermits() != maxPermits) {
				ThreadUtils.randomSleep(1000L);
			}
		}

	}

	public static class CountdownLatch implements Latch {

		private final CountDownLatch delegate;

		CountdownLatch(int maxPermits) {
			this.delegate = new CountDownLatch(maxPermits);
		}

		public long getPermits() {
			return delegate.getCount();
		}

		public boolean acquire() {
			return true;
		}

		public boolean acquire(long timeout, TimeUnit timeUnit) {
			return true;
		}

		public void release() {
			delegate.countDown();
		}

		public void join() {
			try {
				delegate.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

	public static Latch atomicSyncLatch() {
		return atomicSyncLatch(Integer.MAX_VALUE);
	}

	public static Latch atomicSyncLatch(int maxPermits) {
		return new AtomicSyncLatch(maxPermits);
	}

	public static Latch semaphoreLatch(int maxPermits) {
		return new SemaphoreLatch(maxPermits);
	}

	public static Latch countdownLatch(int maxPermits) {
		return new CountdownLatch(maxPermits);
	}

}
