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
 * @author Jimmy Hoff
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
