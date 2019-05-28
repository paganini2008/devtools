package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.multithreads.Execution.RejectedExecutionHandler;
import com.github.paganini2008.devtools.multithreads.Producer.Consumer;

/**
 * 
 * ThreadPoolExecutor
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class ThreadPoolExecutor implements ThreadPool {

	public ThreadPoolExecutor(Executor executor, int maxPermits, long queueTimeout, int queueSize) {
		this.threadPoolConsumer = new ThreadPoolConsumer(executor, maxPermits, queueTimeout, queueSize, this);
		this.running = new AtomicBoolean(true);
	}

	private final ThreadPoolConsumer threadPoolConsumer;
	private final AtomicBoolean running;

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		threadPoolConsumer.setRejectedExecutionHandler(rejectedExecutionHandler);
	}

	public boolean submit(Execution execution) {
		checkRunning();
		return threadPoolConsumer.submit(execution);
	}

	public Promise<?> submitAndWait(Execution execution) {
		checkRunning();
		final Latch latch = Latchs.atomicSyncLatch();
		final AtomicReference<Object> reference = new AtomicReference<Object>();
		final AtomicBoolean cancelled = new AtomicBoolean(false);
		final long startTime = System.currentTimeMillis();
		latch.acquire();
		boolean result = threadPoolConsumer.submit(new FutureExecution(execution, latch, reference, cancelled));
		return new Promise<Object>() {

			public boolean isCancelled() {
				return cancelled.get();
			}

			public boolean isDone() {
				return result && !isCancelled();
			}

			public long getElapsed() {
				return System.currentTimeMillis() - startTime;
			}

			public Object get() {
				latch.join();
				return reference.get();
			}

			public void cancel() {
				cancelled.set(true);
			}

		};
	}

	public int getQueueSize() {
		return threadPoolConsumer.producer.getQueueSize();
	}

	public long getPermits() {
		return threadPoolConsumer.producer.getPermits();
	}

	public boolean isRunning() {
		return running.get();
	}

	private void checkRunning() {
		Assert.isFalse(running.get(), new IllegalStateException("ThreadPool is shutting down or shutted down."));
	}

	public void shutdown() {
		running.getAndSet(true);
		threadPoolConsumer.producer.join();
	}

	private static void checkIfCancelled(boolean cancelled) {
		Assert.isTrue(cancelled, new CancellationException());
	}

	/**
	 * 
	 * FutureExecution
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class FutureExecution implements Execution {

		private final Execution delegate;
		private final Latch latch;
		private final AtomicReference<Object> reference;
		private final AtomicBoolean cancelled;

		FutureExecution(Execution delegate, Latch latch, AtomicReference<Object> reference, AtomicBoolean cancelled) {
			this.delegate = delegate;
			this.latch = latch;
			this.reference = reference;
			this.cancelled = cancelled;
		}

		public Object execute() throws Exception {
			checkIfCancelled(cancelled.get());
			reference.set(delegate.execute());
			return reference.get();
		}

		public void onSuccess(Object result, ThreadPool threadPool) {
			checkIfCancelled(cancelled.get());
			try {
				delegate.onSuccess(result, threadPool);
			} finally {
				reference.set(result);
				latch.release();
			}
		}

		public void onFailure(Exception e, ThreadPool threadPool) {
			try {
				delegate.onFailure(e, threadPool);
			} finally {
				latch.release();
			}
		}

	}

	static class ThreadPoolConsumer implements Consumer<Execution, Object> {

		final ThreadPool threadPool;
		final Producer<Execution, Object> producer;
		RejectedExecutionHandler rejectedExecutionHandler = new RetryRejectedExecutionHandler();

		ThreadPoolConsumer(Executor executor, int maxPermits, long queueTimeout, int queueSize, ThreadPool threadPool) {
			this.producer = new Producer<Execution, Object>(executor, maxPermits, 0.5F, true, queueTimeout, queueSize,
					this);
			this.threadPool = threadPool;
		}

		public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
			this.rejectedExecutionHandler = rejectedExecutionHandler;
		}

		public boolean submit(Execution execution) {
			return producer.submit(execution);
		}

		public Object consume(Execution action) throws Exception {
			return action.execute();
		}

		public void onFailure(Execution action, Exception e) {
			action.onFailure(e, threadPool);
		}

		public void onSuccess(Object result, Execution action) {
			action.onSuccess(result, threadPool);
		}

		public void onRejection(Execution execution) {
			rejectedExecutionHandler.handleRejectedExecution(execution, threadPool);
		}

	}

	public static class RetryRejectedExecutionHandler implements RejectedExecutionHandler {

		private final long limitation;
		private final AtomicLong counter = new AtomicLong(0);

		public RetryRejectedExecutionHandler() {
			this(Long.MAX_VALUE);
		}

		public RetryRejectedExecutionHandler(long limitation) {
			this.limitation = limitation;
		}

		public void handleRejectedExecution(Execution execution, ThreadPool threadPool) {
			if (counter.incrementAndGet() >= limitation) {
				throw new RejectedExecutionException(
						"Execution '" + execution + "' can not be executed in ThreadPool " + threadPool);
			}
			if (threadPool.submit(execution)) {
				counter.decrementAndGet();
			}
		}

	}

	public static class RunnableRejectedActionHandler implements RejectedExecutionHandler {

		public void handleRejectedExecution(Execution execution, ThreadPool threadPool) {
			Object result = null;
			Exception error = null;
			try {
				result = execution.execute();
			} catch (Exception e) {
				error = e;
			} finally {
				if (error != null) {
					execution.onFailure(error, threadPool);
				} else {
					execution.onSuccess(result, threadPool);
				}
			}
		}
	}

	public static class AbortingRejectedActionHandler implements RejectedExecutionHandler {

		public void handleRejectedExecution(Execution execution, ThreadPool threadPool) {
			throw new RejectedExecutionException(
					"Execution '" + execution + "' can not be executed in ThreadPool " + threadPool);
		}

	}

	public static class DiscardedRejectedActionHandler implements RejectedExecutionHandler {

		public void handleRejectedExecution(Execution execution, ThreadPool threadPool) {
		}

	}

}
