package com.github.paganini2008.devtools.multithreads.latch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.AtomicUnsignedLong;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * FairLatch
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public class FairLatch implements Latch {

	private final ThreadLocal<AtomicUnsignedLong> threadLocal = new ThreadLocal<AtomicUnsignedLong>() {

		protected AtomicUnsignedLong initialValue() {
			return new AtomicUnsignedLong(0);
		}

	};

	private final AtomicUnsignedLong sequence = new AtomicUnsignedLong(0);
	private final AtomicInteger counter = new AtomicInteger(0);
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private final long startTime;

	public FairLatch() {
		this.startTime = System.currentTimeMillis();
	}

	public long availablePermits() {
		return 0L;
	}

	public boolean acquire() {
		AtomicUnsignedLong serial = threadLocal.get();
		final long ticket = serial.getAndIncrement();
		while (true) {
			lock.lock();
			try {
				if (ticket == sequence.get()) {
					this.counter.incrementAndGet();
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
		AtomicUnsignedLong serial = threadLocal.get();
		final long ticket = serial.getAndIncrement();
		while (true) {
			lock.lock();
			try {
				if (ticket == sequence.get()) {
					this.counter.incrementAndGet();
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
		AtomicUnsignedLong serial = threadLocal.get();
		long ticket = serial.getAndIncrement();
		return ticket == sequence.get();
	}

	public void release() {
		lock.lock();
		sequence.getAndIncrement();
		counter.decrementAndGet();
		condition.signalAll();
		lock.unlock();
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

	public static void main(String[] args) throws IOException {
		FairLatch latch = new FairLatch();
		ThreadPool threads = ThreadUtils.newCommonPool(10);
		final AtomicInteger score = new AtomicInteger();
		for (int i : Sequence.forEach(0, 1000)) {
			if (latch.acquire(1, TimeUnit.SECONDS)) {
				threads.execute(() -> {

					// ThreadUtils.randomSleep(500L);
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
