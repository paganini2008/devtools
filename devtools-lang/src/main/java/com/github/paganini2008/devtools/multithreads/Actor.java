package com.github.paganini2008.devtools.multithreads;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.latch.Latch;
import com.github.paganini2008.devtools.multithreads.latch.NoopLatch;
import com.github.paganini2008.devtools.multithreads.latch.SemaphoreLatch;

/**
 * 
 * Actor
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class Actor<E> {

	private final Latch latch;
	private final Worker worker;
	private final Executor executor;

	public Actor(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), nThreads);
	}

	public Actor(Executor executor, int maxPermits) {
		this(executor, new ConcurrentLinkedQueue<E>(), maxPermits);
	}

	public Actor(Executor executor, Queue<E> workQueue, int maxPermits) {
		this.worker = new Worker(workQueue);
		this.executor = executor;
		this.latch = maxPermits > 0 ? new SemaphoreLatch(maxPermits) : new NoopLatch();
	}

	public void accept(Iterable<E> iterable) {
		for (E element : iterable) {
			accept(element);
		}
	}

	public void accept(E element) {
		worker.push(element);
		executor.execute(worker);
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
				if (element != null) {
					process(element);
				}
			} finally {
				latch.release();
			}
		}
	}

	public static <E> void forEach(final Iterable<E> iterable, final Consumer<E> consumer) {
		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		Actor<E> actor = new Actor<E>(nThreads) {
			@Override
			protected void process(E element) {
				consumer.accept(element);
			}
		};
		actor.accept(iterable);
		actor.join(true);
	}

	public static void main(String[] args) {
		Actor.forEach(Sequence.forEach(0, 100), e -> {
			System.out.println(e);
		});
	}

}
