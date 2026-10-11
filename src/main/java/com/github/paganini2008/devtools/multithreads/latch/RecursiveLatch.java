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

import com.github.paganini2008.devtools.multithreads.ThreadLocalInteger;

/**
 * 
 * RecursiveLatch
 * <p>
 * A reentrant {@link Latch} decorator: once a thread holds a permit of the delegate latch,
 * further acquisitions by the same thread succeed immediately without consuming more permits.
 * The delegate permit is released when the owning thread's hold count drops to zero, or when
 * {@link #release()} is called from a thread that holds nothing (cross-thread release).
 * <p>
 * Usage constraint: the reentrant count is kept per thread, so this latch is designed for
 * acquire/release pairs executed by the same thread. When a permit is acquired in one thread and
 * released in another (e.g. a submitting thread acquires and a pool worker releases), the delegate
 * permit is returned but the acquiring thread keeps its hold count, and later acquisitions by that
 * thread bypass the delegate (they are no longer limited). Combined with a blocking thread pool,
 * a worker that submits nested tasks without holding a permit itself may block while holding a
 * pool thread, so recursive submission must use a non-blocking timeout or a large enough limit.
 *
 * @author Fred Feng
 * @date 2019-06-10
 * @since 2.0.1
 */
public class RecursiveLatch implements Latch {

	private final Latch delegate;
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
		if (threads.get() > 0 || delegate.acquire()) {
			threads.incrementAndGet();
			return true;
		}
		return false;
	}

	public boolean tryAcquire() {
		if (threads.get() > 0 || delegate.tryAcquire()) {
			threads.incrementAndGet();
			return true;
		}
		return false;
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		if (threads.get() > 0 || delegate.acquire(timeout, timeUnit)) {
			threads.incrementAndGet();
			return true;
		}
		return false;
	}

	public void release() {
		int count = threads.get();
		if (count > 1) {
			threads.decrementAndGet();
			return;
		}
		if (count == 1) {
			threads.reset();
		}
		delegate.release();
	}

	public boolean isLocked() {
		return delegate.isLocked();
	}

	public long join() {
		return delegate.join();
	}

}
