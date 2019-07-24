package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * AsyncThreadPoolmpl
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2016-12
 * @version 1.0
 */
public class AsyncThreadPoolImpl<T> implements AsyncThreadPool<T> {

	private final ThreadPool delegate;
	private final Caller caller;

	AsyncThreadPoolImpl(ThreadPool delegate) {
		this.delegate = delegate;
		this.caller = new Caller();
	}

	public void execute(Runnable command) {
		delegate.execute(command);
	}

	public boolean apply(Runnable r) {
		return delegate.apply(r);
	}

	public boolean submit(Execution2<T> action) {
		caller.workQueue.offer(action);
		return delegate.apply(caller);
	}

	public Promise<T> submitAndWait(Execution2<T> execution) {
		final Latch latch = new CounterLatch(1);
		final AtomicReference<T> reference = new AtomicReference<T>();
		final AtomicBoolean cancelled = new AtomicBoolean(false);
		final long startTime = System.currentTimeMillis();
		latch.acquire();
		boolean result = submit(new FutureExecution<T>(execution, latch, reference, cancelled));
		return new Promise<T>() {

			public boolean isCancelled() {
				return cancelled.get();
			}

			public boolean isDone() {
				return result && !isCancelled();
			}

			public long getElapsed() {
				return System.currentTimeMillis() - startTime;
			}

			public T get() {
				latch.join();
				return reference.get();
			}

			public void cancel() {
				cancelled.set(true);
			}

		};
	}

	public int getPoolSize() {
		return delegate.getPoolSize();
	}

	public void shutdown() {
		delegate.shutdown();
	}

	public int getMaxPoolSize() {
		return delegate.getMaxPoolSize();
	}

	public int getActiveThreadSize() {
		return delegate.getActiveThreadSize();
	}

	public int getIdleThreadSize() {
		return delegate.getIdleThreadSize();
	}

	public int getQueueSize() {
		return delegate.getQueueSize();
	}

	public long getCompletedTaskCount() {
		return delegate.getCompletedTaskCount();
	}
	
	public long getFailedTaskCount() {
		return delegate.getFailedTaskCount();
	}

	public boolean isShutdown() {
		return delegate.isShutdown();
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		delegate.setRejectedExecutionHandler(rejectedExecutionHandler);
	}

	class Caller implements Runnable {

		final Map<Execution2<T>, T> resultArea = new ConcurrentHashMap<Execution2<T>, T>();
		final Queue<Execution2<T>> workQueue = new PriorityBlockingQueue<Execution2<T>>();

		public void run() {
			T result = null;
			Exception error = null;
			final Execution2<T> action = workQueue.poll();
			if (resultArea.containsKey(action)) {
				result = resultArea.remove(action);
				try {
					action.onSuccess(result, AsyncThreadPoolImpl.this);
				} catch (Exception e) {
					error = e;
				} finally {
					if (error != null) {
						action.onFailure(result, error, AsyncThreadPoolImpl.this);
					}
				}
			} else {
				try {
					result = action.execute();
				} catch (Exception e) {
					error = e;
				} finally {
					if (error != null) {
						action.onFailure(error, AsyncThreadPoolImpl.this);
					} else if (result != null) {
						resultArea.put(action, result);
						submit(action);
					}
				}
			}
		}
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
	static class FutureExecution<T> implements Execution2<T> {

		private final Execution2<T> delegate;
		private final Latch latch;
		private final AtomicReference<T> reference;
		private final AtomicBoolean cancelled;

		FutureExecution(Execution2<T> delegate, Latch latch, AtomicReference<T> reference, AtomicBoolean cancelled) {
			this.delegate = delegate;
			this.latch = latch;
			this.reference = reference;
			this.cancelled = cancelled;
		}

		public T execute() throws Exception {
			checkIfCancelled(cancelled.get());
			reference.set(delegate.execute());
			return reference.get();
		}

		public void onSuccess(T result, AsyncThreadPool<T> threadPool) {
			checkIfCancelled(cancelled.get());
			try {
				delegate.onSuccess(result, threadPool);
			} finally {
				reference.set(result);
				latch.release();
			}
		}

		public void onFailure(Exception e, AsyncThreadPool<T> threadPool) {
			try {
				delegate.onFailure(e, threadPool);
			} finally {
				latch.release();
			}
		}

		public void onFailure(T result, Exception e, AsyncThreadPool<T> threadPool) {
			try {
				delegate.onFailure(e, threadPool);
			} finally {
				latch.release();
			}
		}

	}

	public static void main(String[] args) throws IOException {
		AsyncThreadPoolImpl<Integer> threadPool = new AsyncThreadPoolImpl<Integer>(ThreadUtils.newSimplePool(10,1000,Integer.MAX_VALUE));
		final AtomicInteger score = new AtomicInteger(0);
		for (final int i : Sequence.forEach(0, 1000)) {
			threadPool.submit(new Execution2<Integer>() {
				public Integer execute() throws Exception {
					System.out.println(ThreadUtils.currentThreadName() + ": " + i);
					return i;
				}

				public void onSuccess(Integer result, AsyncThreadPool<Integer> threadPool) {
					System.out.println(ThreadUtils.currentThreadName() + ": " + result + ", getQueueSize: " + threadPool.getQueueSize());
					score.incrementAndGet();
				}

			});

			// System.out.println("Answer: " + answer.get());
		}
		//System.in.read();
		threadPool.shutdown();
		System.out.println(score);
	}

}
