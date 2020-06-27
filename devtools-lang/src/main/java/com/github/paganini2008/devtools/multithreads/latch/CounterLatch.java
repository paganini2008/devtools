package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * CounterLatch
 *
 * @author Fred Feng
 * @version 1.0
 */
public class CounterLatch implements Latch {

	private final AtomicInteger counter = new AtomicInteger(0);
	private final int maxPermits;
	private final Lock lock;
	private final Condition condition;
	private final long startTime;

	public CounterLatch(int maxPermits) {
		this(maxPermits, new ReentrantLock());
	}

	public CounterLatch(int maxPermits, Lock lock) {
		this.maxPermits = maxPermits;
		this.lock = lock;
		this.condition = lock.newCondition();
		this.startTime = System.currentTimeMillis();
	}

	public long cons() {
		return counter.get();
	}

	public long availablePermits() {
		return maxPermits - counter.get();
	}

	public boolean acquire() {
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					return true;
				} else {
					try {
						condition.await(1, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						break;
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		final long begin = System.nanoTime();
		long elapsed;
		long nanosTimeout = TimeUnit.NANOSECONDS.convert(timeout, timeUnit);
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					return true;
				} else {
					if (nanosTimeout > 0) {
						try {
							condition.awaitNanos(nanosTimeout);
						} catch (InterruptedException e) {
							break;
						}
						elapsed = (System.nanoTime() - begin);
						nanosTimeout -= elapsed;
					} else {
						break;
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	public boolean tryAcquire() {
		if (counter.get() < maxPermits) {
			counter.incrementAndGet();
			return true;
		}
		return false;
	}

	public void release() {
		if (!isLocked()) {
			return;
		}
		lock.lock();
		counter.decrementAndGet();
		condition.signalAll();
		lock.unlock();
	}

	public long join() {
		while (counter.get() > 0) {
			ThreadUtils.randomSleep(1000L);
		}
		return System.currentTimeMillis() - startTime;
	}

	public boolean isLocked() {
		return counter.get() > 0;
	}

	public static Latch newSingleLatch() {
		return new CounterLatch(1);
	}

	public static Latch newUnlimitedLatch() {
		return new CounterLatch(Integer.MAX_VALUE);
	}

}
