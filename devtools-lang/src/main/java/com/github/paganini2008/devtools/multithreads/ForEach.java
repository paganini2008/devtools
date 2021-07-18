/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * ForEach
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class ForEach<E> {

	private final Latch latch;
	private final Worker worker;
	private final Executor executor;

	public ForEach(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), nThreads * 2);
	}

	public ForEach(Executor executor, int maxPermits) {
		this(executor, new ConcurrentLinkedQueue<E>(), maxPermits);
	}

	public ForEach(Executor executor, Queue<E> workQueue, int maxPermits) {
		this.worker = new Worker(workQueue);
		this.executor = executor;
		this.latch = maxPermits > 0 ? new CounterLatch(maxPermits) : CounterLatch.newUnlimitedLatch();
	}

	public void accept(Iterable<E> iterable) {
		for (E element : iterable) {
			accept(element);
		}
	}

	public void accept(E element) {
		if (element != null) {
			worker.push(element);
			executor.execute(worker);
		}
	}

	public void join(boolean shutdown) {
		latch.join();
		if (shutdown) {
			if (executor instanceof ThreadPool) {
				((ThreadPool) executor).shutdown();
			} else {
				ExecutorUtils.gracefulShutdown(executor, 60000);
			}
		}
	}

	protected abstract void process(E element);

	private class Worker implements Runnable {

		private final Queue<E> queue;

		Worker(Queue<E> queue) {
			this.queue = queue;
		}

		public void push(E element) {
			latch.acquire();
			queue.add(element);
		}

		public void run() {
			E element = queue.poll();
			try {
				process(element);
			} finally {
				latch.release();
			}
		}
	}

	public static <E> void run(final Iterable<E> iterable, final Consumer<E> consumer) {
		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		ForEach<E> forEach = new ForEach<E>(nThreads) {
			@Override
			protected void process(E element) {
				consumer.accept(element);
			}
		};
		forEach.accept(iterable);
		forEach.join(true);
	}

	public static void main(String[] args) {
		ForEach.run(Sequence.forEach(0, 10000), e -> {
			System.out.println(e);
		});
	}

}
