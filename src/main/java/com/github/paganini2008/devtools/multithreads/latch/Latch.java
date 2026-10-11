/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 
 * Latch
 * <p>
 * A permit-based gate used to limit the number of concurrently running tasks. Unlike a
 * {@link java.util.concurrent.locks.Lock}, a permit acquired by one thread may be released by
 * another thread.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Latch {

	/**
	 * Returns the number of permits currently held.
	 * 
	 * @return the number of permits in use
	 */
	long cons();

	/**
	 * Returns the number of permits that can still be acquired.
	 * 
	 * @return the number of available permits
	 */
	long availablePermits();

	/**
	 * Acquires a permit, blocking until one is available.
	 * 
	 * @return true if a permit was acquired, false if the current thread was interrupted
	 *         (the interrupt status is preserved)
	 */
	boolean acquire();

	/**
	 * Acquires a permit only if one is available at the time of invocation.
	 * 
	 * @return true if a permit was acquired
	 */
	boolean tryAcquire();

	/**
	 * Acquires a permit, waiting at most the given time for one to become available.
	 * 
	 * @param timeout
	 *            the maximum time to wait
	 * @param timeUnit
	 *            the unit of {@code timeout}
	 * @return true if a permit was acquired, false on timeout or interruption
	 */
	boolean acquire(long timeout, TimeUnit timeUnit);

	/**
	 * Releases a permit. Releasing when no permit is held has no effect.
	 */
	void release();

	/**
	 * Returns whether at least one permit is currently held.
	 * 
	 * @return true if any permit is held
	 */
	boolean isLocked();

	/**
	 * Blocks until all held permits have been released.
	 * 
	 * @return the elapsed milliseconds since this latch was created
	 */
	long join();

	/**
	 * Runs {@code consumer} for every element on {@code executor}, acquiring a permit before
	 * submitting each element and releasing it when the element has been processed (even if the
	 * consumer throws; if the executor refuses an element by throwing a {@link RuntimeException},
	 * the permit is released and the exception is rethrown). Returns after all elements have been processed, or stops submitting when
	 * the current thread is interrupted while waiting for a permit.
	 * 
	 * @param iterable
	 *            the elements to process
	 * @param executor
	 *            the executor that runs the consumer
	 * @param consumer
	 *            the element processor
	 */
	default <E> void forEach(Iterable<E> iterable, Executor executor, Consumer<E> consumer) {
		for (E e : iterable) {
			if (!acquire()) {
				break;
			}
			try {
				executor.execute(() -> {
					try {
						consumer.accept(e);
					} finally {
						release();
					}
				});
			} catch (RuntimeException ex) {
				// The task was not accepted (rejected, wait queue full, ...): return its permit
				release();
				throw ex;
			}
		}
		join();
	}
}
