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
