package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * SemaphoreLatch
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2014-03
 * @version 1.0
 */
public class SemaphoreLatch implements Latch {

	private final Semaphore latch;
	private final int maxPermits;
	private final long startTime;

	public SemaphoreLatch() {
		this(Integer.MAX_VALUE);
	}

	public SemaphoreLatch(int maxPermits) {
		this.latch = new Semaphore(maxPermits);
		this.maxPermits = maxPermits;
		this.startTime = System.currentTimeMillis();
	}

	public long availablePermits() {
		return maxPermits - latch.availablePermits();
	}

	public boolean acquire() {
		boolean result = true;
		try {
			latch.acquire();
		} catch (InterruptedException e) {
			result = false;
		}
		return result;
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		boolean result = true;
		try {
			result = latch.tryAcquire(timeout, timeUnit);
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

	public static void main(String[] args) throws IOException {
		SemaphoreLatch latch = new SemaphoreLatch();
		ThreadPool threads = ThreadUtils.newCommonPool(10);
		final AtomicInteger score = new AtomicInteger();
		for (int i : Sequence.forEach(0, 100000)) {
			threads.execute(() -> {
				if (latch.acquire(1, TimeUnit.SECONDS)) {
					System.out.println(ThreadUtils.currentThreadName() + ": " + i);
					score.incrementAndGet();
					latch.release();
				}
			});
		}
		latch.join();
		threads.shutdown();
		System.out.println(score);
	}

}
