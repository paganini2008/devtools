package com.github.paganini2008.devtools.multithreads;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * Producer
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public final class Producer<X, R> {

	private final ThreadPool threadPool;
	private final Caller caller;

	public Producer(ThreadPool threadPool, Consumer<X, R> consumer) {
		this(threadPool, new LinkedBlockingQueue<X>(), consumer);
	}

	public Producer(ThreadPool threadPool, Queue<X> workQueue, Consumer<X, R> consumer) {
		this.threadPool = threadPool;
		this.caller = new Caller(workQueue, consumer);
	}

	public void submit(X action) {
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
						submit(action);
					}
				}
			}
		}
	}

	public void join() {
		threadPool.shutdown();
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

		default boolean callback(R result) {
			return result != null;
		}

		default void onFailure(X action, Exception error) {
			error.printStackTrace();
		}

		default void onFailure(R result, X action, Exception error) {
			error.printStackTrace();
		}

		default void onSuccess(R result, X action) {
		}

	}

	public static <X, R> Producer<X, R> common(int nThreads, Consumer<X, R> consumer) {
		return new Producer<X, R>(ThreadUtils.newCommonPool(nThreads), consumer);
	}

	public static <X, R> long executeBatch(int nThreads, Iterator<X> batch, Consumer<X, R> consumer) {
		long start = System.currentTimeMillis();
		Producer<X, R> producer = common(nThreads, consumer);
		while (batch.hasNext()) {
			producer.submit(batch.next());
		}
		producer.join();
		return System.currentTimeMillis() - start;
	}

	public static void main(String[] args) throws Exception {
		long time = Producer.executeBatch(10, Sequence.forEach(1, 100).iterator(), new Consumer<Integer, Long>() {

			public Long consume(Integer action) throws Exception {
				return Long.valueOf(RandomUtils.randomLong(10, 1000000000000L));
			}

			public void onSuccess(Long result, Integer action) {
				System.out.println("Result: " + result);
			}

		});
		System.out.println("Time: " + time);
	}
}
