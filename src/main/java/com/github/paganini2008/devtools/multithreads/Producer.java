package com.github.paganini2008.devtools.multithreads;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * Producer
 * 
 * @author Fred Feng
 * @created 2019-05
 * @version 1.0
 */
public final class Producer<X, R> {

	private final Executor executor;
	private final Consumer<X, R> consumer;
	private final long timeout;
	private final Call call;
	private final Queue<X> waitQueue;
	private final AtomicBoolean running = new AtomicBoolean(true);

	public Producer(Executor executor, int maxPermits, float consumingRatio, boolean sorted, long queueTimeout,
			int queueSize, Consumer<X, R> consumer) {
		this.executor = executor;
		this.timeout = queueTimeout;
		this.call = new Call(sorted ? new PriorityBlockingQueue<X>() : new LinkedBlockingQueue<X>(), maxPermits,
				consumingRatio);
		this.waitQueue = new LinkedBlockingQueue<X>(queueSize);
		this.consumer = consumer;
	}

	public boolean submit(X action) {
		return submit(action, call.primary);
	}

	private boolean submit(X action, Latch latch) {
		boolean result = trySubmit(action, latch);
		if (!result) {
			try {
				waitQueue.add(action);
			} catch (RuntimeException e) {
				consumer.onRejection(action);
			}
		}
		return result;
	}

	private boolean trySubmit(X action, Latch latch) {
		boolean result;
		if (result = latch.acquire(timeout, TimeUnit.MILLISECONDS)) {
			call.workQueue.offer(action);
			if (running.get()) {
				try {
					executor.execute(call);
				} catch (Exception e) {
					running.set(false);
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private final Map<X, R> resultArea = new ConcurrentHashMap<X, R>();

	class Call implements Runnable {

		final Queue<X> workQueue;
		final Latch primary;
		final Latch secondary;

		Call(Queue<X> queue, int maxPermits, float consumingRatio) {
			this.workQueue = queue;
			if (maxPermits < 2) {
				maxPermits = 2;
			}
			if (consumingRatio < 0.5F || consumingRatio >= 1F) {
				throw new IllegalArgumentException("ConsumingRatio must be in (0.5 ~ 1).");
			}
			final int halfPermits = (int) (maxPermits * consumingRatio);
			this.primary = getLatch(maxPermits - halfPermits);
			this.secondary = getLatch(halfPermits);
		}

		public void run() {
			R result = null;
			Exception error = null;
			final X action = workQueue.poll();
			if (resultArea.containsKey(action)) {
				result = resultArea.remove(action);
				try {
					consumer.onSuccess(result, action);
				} catch (Exception e) {
					error = e;
				} finally {
					if (error != null) {
						consumer.onFailure(result, action, error);
					}
					secondary.release();
					submitAgain();
				}
			} else {
				try {
					result = consumer.consume(action);
				} catch (Exception e) {
					error = e;
				} finally {
					if (error != null) {
						consumer.onFailure(action, error);
					} else if (consumer.callback(result)) {
						resultArea.put(action, result);
						submit(action, secondary);
					}
					primary.release();
					submitAgain();
				}
			}
		}

		private void submitAgain() {
			final X action = waitQueue.poll();
			if (action != null) {
				Latch latch = resultArea.containsKey(action) ? secondary : primary;
				submit(action, latch);
			}
		}
	}

	protected Latch getLatch(int maxPermits) {
		return Latchs.semaphoreLatch(maxPermits);
	}

	public int getQueueSize() {
		return waitQueue.size();
	}

	public void join() {
		doJoin();
		while (!waitQueue.isEmpty()) {
			;
		}
		doJoin();
		running.set(false);
		ExecutorUtils.gracefulShutdown(executor, 60000);
	}

	private void doJoin() {
		call.primary.join();
		call.secondary.join();
	}

	public long getPermits() {
		return call.primary.getPermits() + call.secondary.getPermits();
	}

	/**
	 * 
	 * Consumer
	 * 
	 * @author Fred Feng
	 * @created 2019-05
	 * @version 1.0
	 */
	public static interface Consumer<X, R> {

		R consume(X action) throws Exception;

		default boolean callback(R result) {
			return result != null;
		}

		default void onRejection(X action) {
		}

		default void onFailure(X action, Exception error) {
			error.printStackTrace();
		}

		default void onFailure(Object result, X action, Exception error) {
			error.printStackTrace();
		}

		default void onSuccess(R result, X action) {
		}

	}

	public static <X, R> Producer<X, R> common(int nThreads, Consumer<X, R> consumer) {
		return common(nThreads, 1000L, consumer);
	}

	public static <X, R> Producer<X, R> common(int nThreads, long timeout, Consumer<X, R> consumer) {
		return common(nThreads, Integer.MAX_VALUE, timeout, consumer);
	}

	public static <X, R> Producer<X, R> common(int nThreads, int maxPermits, long timeout, Consumer<X, R> consumer) {
		Executor executor = ExecutorUtils.commonPool(nThreads);
		return new Producer<X, R>(executor, maxPermits, 0.5F, false, timeout, Integer.MAX_VALUE, consumer);
	}

	public static <X, R> long executeBatch(int nThreads, Iterator<X> batch, Consumer<X, R> consumer) {
		return executeBatch(nThreads, Integer.MAX_VALUE, batch, consumer);
	}

	public static <X, R> long executeBatch(int nThreads, int maxPermits, Iterator<X> batch, Consumer<X, R> consumer) {
		long start = System.currentTimeMillis();
		Producer<X, R> producer = common(nThreads, maxPermits, 1000L, consumer);
		while (batch.hasNext()) {
			producer.submit(batch.next());
		}
		producer.join();
		return System.currentTimeMillis() - start;
	}

	public static void main(String[] args) throws Exception {
		long time = Producer.executeBatch(10, 100, Sequence.forEach(1, 1000000).iterator(),
				new Consumer<Integer, Long>() {

					public Long consume(Integer action) throws Exception {
						return Long.valueOf(RandomUtils.randomInt(0, 1000000));
					}

					public void onSuccess(Long result, Integer action) {
						// ThreadUtils.randomSleep(3000L);
						System.out.println("Result: " + result);
					}

				});
		System.out.println("Time: " + time);
	}
}
