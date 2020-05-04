package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * Latch
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Latch {

	long availablePermits();

	boolean acquire();

	boolean tryAcquire();

	boolean acquire(long timeout, TimeUnit timeUnit);

	void release();

	boolean isLocked();

	long join();

	default <E> void forEach(Iterable<E> iterable, Consumer<E> consumer) {
		ThreadPool threadPool = ThreadUtils.commonPool();
		for (E e : iterable) {
			acquire();
			threadPool.apply(() -> {
				consumer.accept(e);
				release();
			});
		}
		join();
		threadPool.shutdown();
	}
}
