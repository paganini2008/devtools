package com.github.paganini2008.devtools.multithreads.latch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.AtomicUnsignedLong;
import com.github.paganini2008.devtools.multithreads.ThreadLocalInteger;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
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
	private final AtomicUnsignedLong sequence = new AtomicUnsignedLong(0);
	private final AtomicInteger counter = new AtomicInteger(0);
	private final long startTime;

	public FairLatch() {
		this.startTime = System.currentTimeMillis();
	}

	public long availablePermits() {
		return 0L;
	}

	public boolean acquire() {
		final long ticket = tickets.getAndIncrement();
		return ThreadUtils.wait(this, () -> {
			if (ticket == sequence.get()) {
				this.counter.incrementAndGet();
				return true;
			}
			return false;
		});
	}

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		final long ticket = tickets.getAndIncrement();
		return ThreadUtils.wait(this, () -> {
			if (ticket == sequence.get()) {
				this.counter.incrementAndGet();
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

	public static void main(String[] args) throws IOException {
		FairLatch latch = new FairLatch();
		ThreadPool threads = ThreadUtils.commonPool(10);
		final AtomicInteger score = new AtomicInteger();
		for (int i : Sequence.forEach(0, 100000)) {
			if (latch.acquire(1, TimeUnit.SECONDS)) {
				threads.execute(() -> {

					//ThreadUtils.randomSleep(500L);
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
