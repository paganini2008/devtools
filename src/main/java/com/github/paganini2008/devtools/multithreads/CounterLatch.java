package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * CounterLatch
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2014-03
 * @version 1.0
 */
public class CounterLatch implements Latch {

	private final AtomicLong counter = new AtomicLong(0);
	private final long maxPermits;
	private final Lock lock;
	private final Condition condition;
	private final long startTime;

	public CounterLatch() {
		this(Integer.MAX_VALUE);
	}

	public CounterLatch(long maxPermits) {
		this(maxPermits, new ReentrantLock());
	}

	public CounterLatch(long maxPermits, Lock lock) {
		this.maxPermits = maxPermits;
		this.lock = lock;
		this.condition = lock.newCondition();
		this.startTime = System.currentTimeMillis();
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
						condition.await();
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

	public void release() {
		if (!isLocked()) {
			return;
		}
		lock.lock();
		condition.signalAll();
		counter.decrementAndGet();
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

}
