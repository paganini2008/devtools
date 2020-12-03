package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 
 * Latch
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Latch {
	
	long cons();

	long availablePermits();

	boolean acquire();

	boolean tryAcquire();

	boolean acquire(long timeout, TimeUnit timeUnit);

	void release();

	boolean isLocked();

	long join();

	default <E> void forEach(Iterable<E> iterable, Executor executor, Consumer<E> consumer) {
		for (E e : iterable) {
			acquire();
			executor.execute(() -> {
				consumer.accept(e);
				release();
			});
		}
		join();
	}
}
