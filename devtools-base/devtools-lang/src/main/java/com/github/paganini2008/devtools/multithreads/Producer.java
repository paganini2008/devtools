package com.github.paganini2008.devtools.multithreads;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * Producer
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2014-03
 * @version 1.0
 */
public final class Producer<X, R> {

	private final Executor threadPool;
	private final Caller caller;

	public Producer(Executor executor, Consumer<X, R> consumer) {
		this(executor, new LinkedBlockingQueue<X>(), consumer);
	}

	public Producer(Executor executor, Queue<X> workQueue, Consumer<X, R> consumer) {
		this.threadPool = executor;
		this.caller = new Caller(workQueue, consumer);
	}

	public void produce(X action) {
		caller.workQueue.offer(action);
		threadPool.execute(caller);
	}

	class Caller implements Runnable {

		final Map<X, R> resultArea = new ConcurrentHashMap<X, R>();
		final Queue<X> workQueue;
		final Consumer<X, R> consumer;

		Caller(Queue<X> workQueue, Consumer<X, R> consumer) {
			this.workQueue = workQueue;
			this.consumer = consumer;
		}

		public void run() {
			R result = null;
			Exception cause = null;
			final X action = workQueue.poll();
			if (resultArea.containsKey(action)) {
				try {
					result = consumer.onSuccess(resultArea.remove(action), action);
				} catch (Exception e) {
					cause = e;
				} finally {
					if (cause != null) {
						consumer.onFailure(action, cause);
					} else if (consumer.shouldCallback(result)) {
						resultArea.put(action, result);
						produce(action);
					}
				}
			} else {
				try {
					result = consumer.consume(action);
				} catch (Exception e) {
					cause = e;
				} finally {
					if (cause != null) {
						consumer.onFailure(action, cause);
					} else if (consumer.shouldCallback(result)) {
						resultArea.put(action, result);
						produce(action);
					}
				}
			}
		}
	}

	public void join() {
		if (threadPool instanceof ThreadPool) {
			((ThreadPool) threadPool).shutdown();
		}
		ExecutorUtils.gracefulShutdown(threadPool, 60000L);
	}

	/**
	 * 
	 * Consumer
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	public static interface Consumer<X, R> {

		R consume(X action) throws Exception;

		default boolean shouldCallback(R result) {
			return result != null;
		}

		default void onFailure(X action, Exception cause) {
			cause.printStackTrace();
		}

		default R onSuccess(R result, X action) {
			return null;
		}

	}

	public static <X, R> long executeBatch(Iterator<X> batch, int nThreads, Consumer<X, R> consumer) {
		long start = System.currentTimeMillis();
		Producer<X, R> producer = new Producer<X, R>(ThreadUtils.newCommonPool(nThreads), consumer);
		while (batch.hasNext()) {
			producer.produce(batch.next());
		}
		producer.join();
		return System.currentTimeMillis() - start;
	}

	public static void main(String[] args) throws Exception {
		long time = Producer.executeBatch(Sequence.forEach(1, 100).iterator(), 10, new Consumer<Integer, Long>() {

			public Long consume(Integer action) throws Exception {
				return Long.valueOf(action);
			}

			public Long onSuccess(Long result, Integer action) {
				System.out.println("Result: " + result);
				return null;
			}

		});
		System.out.println("Time: " + time);
	}
}
