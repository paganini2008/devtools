package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 
 * NoopLatch
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NoopLatch implements Latch {

	private final long startTime = System.currentTimeMillis();

	@Override
	public long cons() {
		return 0L;
	}
	
	@Override
	public long availablePermits() {
		return Long.MAX_VALUE;
	}

	@Override
	public boolean acquire() {
		return true;
	}

	@Override
	public boolean tryAcquire() {
		return true;
	}

	@Override
	public boolean acquire(long timeout, TimeUnit timeUnit) {
		return true;
	}

	@Override
	public void release() {
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public long join() {
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public <E> void forEach(Iterable<E> iterable, Executor executor, Consumer<E> consumer) {
		throw new UnsupportedOperationException();
	}

}
