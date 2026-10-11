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

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * SemaphoreLatch
 * <p>
 * A {@link Latch} backed by a {@link Semaphore}, optionally fair (first-come, first-served).
 * 
 * @author Fred Feng
 * @date 2019-06-06
 * @since 2.0.1
 */
public class SemaphoreLatch implements Latch {

	private final Semaphore latch;
	private final int maxPermits;
	private final long startTime;
	private final AtomicInteger held = new AtomicInteger(0);
	private final Object monitor = new Object();

	public SemaphoreLatch() {
		this(1);
	}

	public SemaphoreLatch(int maxPermits) {
		this(maxPermits, false);
	}

	/**
	 * Creates a latch with the given number of permits.
	 * 
	 * @param maxPermits
	 *            the maximum number of permits
	 * @param fair
	 *            true to grant permits in first-in first-out order
	 */
	public SemaphoreLatch(int maxPermits, boolean fair) {
		this.latch = new Semaphore(maxPermits, fair);
		this.maxPermits = maxPermits;
		this.startTime = System.currentTimeMillis();
	}

	public long cons() {
		return held.get();
	}

	public long availablePermits() {
		return latch.availablePermits();
	}

	public boolean tryAcquire() {
		if (latch.tryAcquire(1)) {
			held.incrementAndGet();
			return true;
		}
		return false;
	}

	public boolean acquire() {
		try {
			latch.acquire(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
		held.incrementAndGet();
		return true;
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		try {
			if (latch.tryAcquire(1, timeout, timeUnit)) {
				held.incrementAndGet();
				return true;
			}
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public void release() {
		int current;
		do {
			current = held.get();
			if (current <= 0) {
				return;
			}
		} while (!held.compareAndSet(current, current - 1));
		latch.release();
		if (current == 1) {
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
	}

	public long join() {
		synchronized (monitor) {
			while (held.get() > 0) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
		return System.currentTimeMillis() - startTime;
	}

	public boolean isLocked() {
		return held.get() > 0;
	}

	/**
	 * Returns the maximum number of permits.
	 * 
	 * @return the maximum number of permits
	 */
	public int getMaxPermits() {
		return maxPermits;
	}

}
