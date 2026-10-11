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
package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * CounterLatch
 * <p>
 * A {@link Latch} backed by an atomic counter limited to a maximum number of permits.
 *
 * @author Fred Feng
 * @date 2019-06-06
 * @since 2.0.1
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
		if (tryAcquire()) {
			return true;
		}
		lock.lock();
		try {
			while (!tryAcquire()) {
				condition.await();
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		if (tryAcquire()) {
			return true;
		}
		long nanosTimeout = timeUnit.toNanos(timeout);
		lock.lock();
		try {
			while (!tryAcquire()) {
				if (nanosTimeout <= 0) {
					return false;
				}
				nanosTimeout = condition.awaitNanos(nanosTimeout);
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean tryAcquire() {
		int current;
		do {
			current = counter.get();
			if (current >= maxPermits) {
				return false;
			}
		} while (!counter.compareAndSet(current, current + 1));
		return true;
	}

	public void release() {
		int current;
		do {
			current = counter.get();
			if (current <= 0) {
				return;
			}
		} while (!counter.compareAndSet(current, current - 1));
		lock.lock();
		try {
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public long join() {
		lock.lock();
		try {
			while (counter.get() > 0) {
				condition.await();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			lock.unlock();
		}
		return System.currentTimeMillis() - startTime;
	}

	public boolean isLocked() {
		return counter.get() > 0;
	}

	/**
	 * Creates a latch with a single permit.
	 * 
	 * @return a new latch
	 */
	public static Latch newSingleLatch() {
		return new CounterLatch(1);
	}

	/**
	 * Creates a latch whose permits are practically unlimited ({@link Integer#MAX_VALUE}).
	 * 
	 * @return a new latch
	 */
	public static Latch newUnlimitedLatch() {
		return new CounterLatch(Integer.MAX_VALUE);
	}

}
