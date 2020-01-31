package com.github.paganini2008.devtools.multithreads.latch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RecursiveLatch
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2019-05
 * @version 1.0
 */
public class RecursiveLatch implements Latch {

	private final Latch delegate;
	private final Lock lock = new ReentrantLock();
	private final ThreadLocal<AtomicInteger> counter = new ThreadLocal<AtomicInteger>() {
		protected AtomicInteger initialValue() {
			return new AtomicInteger(0);
		}
	};

	public RecursiveLatch() {
		this(1);
	}

	public RecursiveLatch(int maxPermits) {
		this(new CounterLatch(maxPermits));
	}

	public RecursiveLatch(Latch delegate) {
		this.delegate = delegate;
	}

	public long availablePermits() {
		return delegate.availablePermits();
	}

	public boolean acquire() {
		lock.lock();
		try {
			AtomicInteger threads = counter.get();
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
			AtomicInteger threads = counter.get();
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
			AtomicInteger threads = counter.get();
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
			AtomicInteger threads = counter.get();
			if (threads.get() > 0) {
				threads.decrementAndGet();
			}
			if (threads.get() == 0) {
				counter.remove();
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

	public static void main(String[] args) throws IOException {
		RecursiveLatch latch = new RecursiveLatch(2);
		ThreadPool threads = ThreadUtils.commonPool(10);
		final AtomicInteger score = new AtomicInteger();
		for (int i : Sequence.forEach(0, 100)) {
			if (latch.acquire(1, TimeUnit.SECONDS)) {
				threads.execute(() -> {

					ThreadUtils.randomSleep(500L);
					System.out.println(ThreadUtils.currentThreadName() + ": " + i);
					score.incrementAndGet();
					latch.release();
				});
			}
		}
		latch.join();
		threads.shutdown();
		System.out.println(score);
	}

}
