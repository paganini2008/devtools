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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.multithreads.AtomicLongSequence;
import com.github.paganini2008.devtools.multithreads.ThreadLocalInteger;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * FairLatch
 *
 * @author Fred Feng
 * @version 1.0
 */
public class FairLatch implements Latch {

	private final ThreadLocalInteger tickets = new ThreadLocalInteger(0);
	private final AtomicLongSequence sequence = new AtomicLongSequence(0);
	private final AtomicInteger counter = new AtomicInteger(0);
	private final long startTime;

	public FairLatch() {
		this.startTime = System.currentTimeMillis();
	}
	
	public long cons() {
		return counter.get();
	}

	public long availablePermits() {
		return 0L;
	}

	public boolean acquire() {
		final long ticket = tickets.getAndIncrement();
		return ThreadUtils.wait(this, () -> {
			if (ticket == sequence.get()) {
				counter.incrementAndGet();
				return true;
			}
			return false;
		});
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		final long ticket = tickets.getAndIncrement();
		return ThreadUtils.wait(this, () -> {
			if (ticket == sequence.get()) {
				counter.incrementAndGet();
				return true;
			}
			return false;
		}, TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
	}

	public boolean tryAcquire() {
		long ticket = tickets.getAndIncrement();
		return ticket == sequence.get();
	}

	public void release() {
		ThreadUtils.notify(this, () -> {
			sequence.getAndIncrement();
			counter.decrementAndGet();
			return true;
		});
	}

	public boolean isLocked() {
		return counter.get() > 0;
	}

	public long join() {
		while (counter.get() > 0) {
			ThreadUtils.randomSleep(1000L);
		}
		return System.currentTimeMillis() - startTime;
	}

}
