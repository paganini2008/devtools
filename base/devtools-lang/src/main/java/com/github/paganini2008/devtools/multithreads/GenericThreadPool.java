package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * GenericThreadPool
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2016-02
 * @version 1.0
 */
public class GenericThreadPool extends ThreadPoolExecutor implements ThreadPool {

	public GenericThreadPool(int maxPoolSize, long timeout, int queueSize, ThreadFactory threadFactory) {
		this(maxPoolSize, new CounterLatch(maxPoolSize), timeout, queueSize, threadFactory);
	}

	public GenericThreadPool(int maxPoolSize, Latch latch, long timeout, int queueSize, ThreadFactory threadFactory) {
		super(maxPoolSize, maxPoolSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory,
				new ThreadPoolExecutor.AbortPolicy());
		this.latch = latch;
		this.timeout = timeout;
		this.waitQueue = new LinkedBlockingQueue<Runnable>(queueSize);
	}

	private final Latch latch;
	private final long timeout;
	private final Queue<Runnable> waitQueue;
	private final AtomicInteger failedCount = new AtomicInteger(0);
	private RejectedExecutionHandler rejectedExecutionHandler;

	public boolean apply(Runnable task) {
		boolean acquired = timeout > 0 ? latch.acquire(timeout, TimeUnit.MILLISECONDS)
				: timeout == 0 ? latch.tryAcquire() : latch.acquire();
		if (acquired) {
			super.execute(task);
		} else {
			try {
				waitQueue.add(task);
			} catch (RuntimeException e) {
				if (rejectedExecutionHandler != null) {
					rejectedExecutionHandler.handleRejectedExecution(task, this);
				} else {
					throw new IllegalStateException("WaitQueue Full!");
				}
			}
		}
		return acquired;
	}

	public <R> Promise<R> submit(final Action<R> action) {
		final Reference<R> reference = new Reference<R>();
		final Future<R> future = super.submit(() -> {
			return action.execute();
		});
		apply(new ActionFutureTask<R>(future, action, reference, this));
		return new DefaultPromise<R>(reference);
	}

	public int getMaxPoolSize() {
		return getMaximumPoolSize();
	}

	public int getQueueSize() {
		return waitQueue.size();
	}

	public int getActiveThreadSize() {
		return getActiveCount();
	}

	public int getIdleThreadSize() {
		return getMaxPoolSize() - getActiveThreadSize();
	}

	public long getFailedTaskCount() {
		return failedCount.get();
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	public void execute(Runnable command) {
		apply(command);
	}

	protected final void afterExecute(Runnable runnable, Throwable e) {
		super.afterExecute(runnable, e);
		latch.release();
		if (e != null) {
			failedCount.incrementAndGet();
		}
		Runnable prev = waitQueue.poll();
		if (prev != null) {
			execute(prev);
		}
	}

	public void shutdown() {
		latch.join();
		super.shutdown();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[GenericThreadPool]: ");
		str.append(", maxPoolSize=").append(getMaxPoolSize());
		str.append(", activeThreadSize=").append(getActiveThreadSize());
		str.append(", idleThreadSize=").append(getIdleThreadSize());
		str.append(", completedTaskCount=").append(getCompletedTaskCount());
		str.append(", failedTaskCount=").append(getFailedTaskCount());
		str.append(", queueSize=").append(getQueueSize());
		return str.toString();
	}

	static class DefaultPromise<R> implements Promise<R> {

		final Reference<R> reference;
		final long startTime;
		volatile boolean cancelled;
		volatile boolean done;

		DefaultPromise(Reference<R> reference) {
			this.reference = reference;
			this.startTime = System.currentTimeMillis();
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public boolean isDone() {
			return done || cancelled;
		}

		public long getElapsed() {
			return System.currentTimeMillis() - startTime;
		}

		public R get() {
			while (!isDone()) {
				synchronized (reference) {
					if (!reference.isDone()) {
						try {
							reference.wait();
						} catch (InterruptedException ignored) {
							break;
						}
					}
				}
				done = reference.isDone();
			}
			return reference.get();
		}

		public R get(long timeout) {
			if (!isDone()) {
				synchronized (reference) {
					if (!reference.isDone()) {
						try {
							reference.wait(timeout);
						} catch (InterruptedException ignored) {
						}
					}
				}
			}
			done = reference.isDone();
			return reference.get();
		}

		public void cancel() {
			if (!isDone()) {
				cancelled = true;
				synchronized (reference) {
					reference.notifyAll();
				}
			}
		}
	}

	static class Reference<R> {

		R result;
		volatile boolean done;

		public R get() {
			return result;
		}

		public void set(R result) {
			this.result = result;
		}

		public boolean isDone() {
			return done;
		}

		public void setDone(boolean done) {
			this.done = done;
		}

	}

	static class ActionFutureTask<R> implements Runnable {

		ActionFutureTask(Future<R> delegate, Action<R> action, Reference<R> reference, ThreadPool threadPool) {
			this.delegate = delegate;
			this.action = action;
			this.reference = reference;
			this.threadPool = threadPool;
		}

		final Map<Action<R>, R> results = new HashMap<Action<R>, R>();
		final Future<R> delegate;
		final Action<R> action;
		final Reference<R> reference;
		final ThreadPool threadPool;

		public void run() {
			R result = null;
			if (results.containsKey(action)) {
				result = action.onReaction(results.remove(action), threadPool);
			} else {
				try {
					result = delegate.get();
				} catch (Exception e) {
					if (e instanceof ExecutionException) {
						action.onFailure(e, threadPool);
					}
				}
			}
			if (action.shouldReact(result)) {
				results.put(action, result);
				reference.set(result);
				threadPool.apply(this);
			} else {
				synchronized (reference) {
					reference.notifyAll();
					reference.setDone(true);
				}
			}
		}

	}

	public static void main(String[] args) throws IOException {
		GenericThreadPool threadPool = new GenericThreadPool(10, 1000L, Integer.MAX_VALUE, Executors.defaultThreadFactory());
		List<Promise<Long>> promises = new CopyOnWriteArrayList<Promise<Long>>();
		for (final int i : Sequence.forEach(0, 100)) {
			Promise<Long> p = threadPool.submit(new Action<Long>() {

				public Long execute() throws Exception {
					ThreadUtils.randomSleep(1000L);
					System.out.println(ThreadUtils.currentThreadName() + " say: " + i);
					return new Long(i);
				}

			});
			promises.add(p);
		}
		for (Promise<Long> p : promises) {
			System.out.println("***: " + p.get());
		}
		System.in.read();
		threadPool.shutdown();
		System.out.println("SimpleThreadPool.main()");
	}

}
