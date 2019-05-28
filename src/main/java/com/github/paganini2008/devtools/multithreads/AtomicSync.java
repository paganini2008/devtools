package com.github.paganini2008.devtools.multithreads;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * AtomicSync
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public final class AtomicSync implements Serializable {

	private static final long serialVersionUID = 937405547342007201L;
	private final AtomicLong counter;
	private final long initialValue;

	public AtomicSync() {
		this(0);
	}

	public AtomicSync(long initialValue) {
		this.initialValue = initialValue;
		this.counter = new AtomicLong(initialValue);
	}

	public boolean countUp() {
		counter.incrementAndGet();
		return true;
	}

	public boolean countUp(int limitation, long timeout, TimeUnit timeUnit) {
		long s = System.currentTimeMillis();
		long t = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
		while (get() > limitation) {
			if (System.currentTimeMillis() - s > t) {
				return false;
			}
			test();
		}
		counter.incrementAndGet();
		return true;
	}

	public long countDown() {
		if (get() > initialValue) {
			return counter.decrementAndGet();
		} else {
			return initialValue;
		}
	}

	public long get() {
		return Math.max(initialValue, counter.get());
	}

	public boolean isLocked() {
		return get() > initialValue;
	}

	public void join() {
		while (isLocked()) {
			test();
		}
	}

	public void join(long timeout, TimeUnit timeUnit) {
		long s = System.currentTimeMillis();
		long t = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
		while (isLocked()) {
			if (System.currentTimeMillis() - s > t) {
				break;
			}
			test();
		}
	}

	protected void test() {
		ThreadUtils.randomSleep(1000L);
	}

	public String toString() {
		return String.valueOf(get());
	}
}
