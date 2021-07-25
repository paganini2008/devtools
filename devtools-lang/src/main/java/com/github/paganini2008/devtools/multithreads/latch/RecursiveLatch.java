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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.paganini2008.devtools.multithreads.ThreadLocalInteger;

/**
 * 
 * RecursiveLatch
 *
 * @author Fred Feng
 * @version 1.0
 */
public class RecursiveLatch implements Latch {

	private final Latch delegate;
	private final Lock lock = new ReentrantLock();
	private final ThreadLocalInteger threads = new ThreadLocalInteger(0);

	public RecursiveLatch() {
		this(1);
	}

	public RecursiveLatch(int maxPermits) {
		this(new CounterLatch(maxPermits));
	}

	public RecursiveLatch(Latch delegate) {
		this.delegate = delegate;
	}

	public long cons() {
		return delegate.cons();
	}

	public long availablePermits() {
		return delegate.availablePermits();
	}

	public boolean acquire() {
		lock.lock();
		try {
			boolean acquired = true;
			if (threads.get() == 0) {
				acquired = delegate.acquire();
			}
			if (acquired) {
				threads.incrementAndGet();
				return true;
			} else {
				return false;
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean tryAcquire() {
		lock.lock();
		try {
			boolean acquired = true;
			if (threads.get() == 0) {
				acquired = delegate.tryAcquire();
			}
			if (acquired) {
				threads.incrementAndGet();
				return true;
			} else {
				return false;
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		lock.lock();
		try {
			boolean acquired = true;
			if (threads.get() == 0) {
				acquired = delegate.acquire(timeout, timeUnit);
			}
			if (acquired) {
				threads.incrementAndGet();
				return true;
			} else {
				return false;
			}
		} finally {
			lock.unlock();
		}
	}

	public void release() {
		lock.lock();
		try {
			if (threads.get() > 0) {
				threads.decrementAndGet();
			}
			if (threads.get() == 0) {
				delegate.release();
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean isLocked() {
		return delegate.isLocked();
	}

	public long join() {
		return delegate.join();
	}

}
