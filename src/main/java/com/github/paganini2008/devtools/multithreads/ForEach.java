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
package com.github.paganini2008.devtools.multithreads;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * ForEach
 * <p>
 * Processes elements concurrently on an {@link Executor}. The number of elements accepted but not
 * yet processed is bounded by {@code maxPermits}: {@link #accept(Object)} blocks when the limit is
 * reached. {@link #join(boolean)} waits for all accepted elements to be processed.
 *
 * @author Fred Feng
 * @date 2020-05-04
 * @since 2.0.1
 */
public abstract class ForEach<E> {

	private final Latch latch;
	private final Worker worker;
	private final Executor executor;
	private final ThreadLocal<Boolean> inWorker = new ThreadLocal<Boolean>();

	public ForEach(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), nThreads * 2);
	}

	public ForEach(Executor executor, int maxPermits) {
		this(executor, new ConcurrentLinkedQueue<E>(), maxPermits);
	}

	/**
	 * Creates a ForEach.
	 * 
	 * @param executor
	 *            the executor processing elements
	 * @param workQueue
	 *            the queue holding pending elements (determines processing order); must be thread
	 *            safe
	 * @param maxPermits
	 *            the maximum number of pending elements; a value &lt;= 0 means unlimited
	 */
	public ForEach(Executor executor, Queue<E> workQueue, int maxPermits) {
		this.worker = new Worker(workQueue);
		this.executor = executor;
		this.latch = maxPermits > 0 ? new CounterLatch(maxPermits) : CounterLatch.newUnlimitedLatch();
	}

	/**
	 * Accepts every non-null element of the iterable.
	 * 
	 * @param iterable
	 *            the elements, null is ignored
	 */
	public void accept(Iterable<E> iterable) {
		if (iterable == null) {
			return;
		}
		for (E element : iterable) {
			accept(element);
		}
	}

	/**
	 * Schedules an element for processing, blocking while the number of pending elements has
	 * reached {@code maxPermits}. A null element is ignored.
	 * <p>
	 * When called from {@link #process(Object)} (i.e. from a thread that is processing an element
	 * of this ForEach) and no permit is available, the element is processed directly in the
	 * calling thread instead of blocking, because waiting there could deadlock: the permit can only
	 * be released by processing threads that may all be waiting in the same way.
	 * <p>
	 * If the executor refuses the element by throwing a {@link RuntimeException}, the element is
	 * discarded, its permit is released and the exception is rethrown.
	 * 
	 * @param element
	 *            the element
	 */
	public void accept(E element) {
		if (element != null) {
			if (isWorkerThread()) {
				if (!latch.tryAcquire()) {
					// Caller runs: blocking here could deadlock all processing threads
					process(element);
					return;
				}
				worker.queue.add(element);
			} else if (!worker.push(element)) {
				return;
			}
			try {
				executor.execute(worker);
			} catch (RuntimeException e) {
				// Discard the element so that it is not processed later in place of another one
				worker.queue.remove(element);
				latch.release();
				throw e;
			}
		}
	}

	/**
	 * Waits until all accepted elements have been processed.
	 * <p>
	 * When called from {@link #process(Object)}, waiting would deadlock (the calling thread holds
	 * a pending element itself), so this method returns immediately; if {@code shutdown} is true,
	 * waiting and shutting down are carried out asynchronously by a background thread.
	 * 
	 * @param shutdown
	 *            whether to shut down the executor afterwards
	 */
	public void join(final boolean shutdown) {
		if (isWorkerThread()) {
			if (shutdown) {
				Thread t = new Thread(() -> join(true), "ForEach-join");
				t.setDaemon(true);
				t.start();
			}
			return;
		}
		latch.join();
		if (shutdown) {
			if (executor instanceof ThreadPool) {
				((ThreadPool) executor).shutdown();
			} else {
				ExecutorUtils.gracefulShutdown(executor, 60000);
			}
		}
	}

	private boolean isWorkerThread() {
		return inWorker.get() != null;
	}

	/**
	 * Processes one element; called concurrently from executor threads.
	 * 
	 * @param element
	 *            the element, never null
	 */
	protected abstract void process(E element);

	private class Worker implements Runnable {

		private final Queue<E> queue;

		Worker(Queue<E> queue) {
			this.queue = queue;
		}

		boolean push(E element) {
			if (!latch.acquire()) {
				return false;
			}
			queue.add(element);
			return true;
		}

		public void run() {
			final Boolean previous = inWorker.get();
			inWorker.set(Boolean.TRUE);
			E element = queue.poll();
			try {
				if (element != null) {
					process(element);
				}
			} finally {
				latch.release();
				if (previous == null) {
					inWorker.remove();
				}
			}
		}
	}

	/**
	 * Applies {@code consumer} to every element concurrently using a temporary thread pool and
	 * returns after all elements have been processed.
	 * 
	 * @param iterable
	 *            the elements
	 * @param consumer
	 *            the element processor
	 */
	public static <E> void run(final Iterable<E> iterable, final Consumer<E> consumer) {
		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		ForEach<E> forEach = new ForEach<E>(nThreads) {
			@Override
			protected void process(E element) {
				consumer.accept(element);
			}
		};
		try {
			forEach.accept(iterable);
		} finally {
			forEach.join(true);
		}
	}
}
