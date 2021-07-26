/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * CounterLatch
 *
 * @author Fred Feng
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
