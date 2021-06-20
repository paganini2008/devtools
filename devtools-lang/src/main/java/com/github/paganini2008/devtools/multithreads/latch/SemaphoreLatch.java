/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * SemaphoreLatch
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class SemaphoreLatch implements Latch {

	private final Semaphore latch;
	private final int maxPermits;
	private final long startTime;

	public SemaphoreLatch() {
		this(1);
	}

	public SemaphoreLatch(int maxPermits) {
		this.latch = new Semaphore(maxPermits);
		this.maxPermits = maxPermits;
		this.startTime = System.currentTimeMillis();
	}

	public long cons() {
		return maxPermits - availablePermits();
	}

	public long availablePermits() {
		return latch.availablePermits();
	}

	public boolean tryAcquire() {
		return latch.tryAcquire(1);
	}

	public boolean acquire() {
		boolean result = true;
		try {
			latch.acquire(1);
		} catch (InterruptedException e) {
			result = false;
		}
		return result;
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		boolean result = true;
		try {
			result = latch.tryAcquire(1, timeout, timeUnit);
		} catch (InterruptedException e) {
			result = false;
		}
		return result;
	}

	public void release() {
		if (isLocked()) {
			latch.release();
		}
	}

	public long join() {
		while (latch.availablePermits() != maxPermits) {
			ThreadUtils.randomSleep(1000L);
		}
		return System.currentTimeMillis() - startTime;
	}

	public boolean isLocked() {
		return latch.availablePermits() != maxPermits;
	}

}
