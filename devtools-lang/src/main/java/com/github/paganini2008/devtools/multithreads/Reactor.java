package com.github.paganini2008.devtools.multithreads;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * Reactor
 *
 * @author Fred Feng
 * @since 1.0
 */
public class Reactor<X, R> extends ForEach<X> {

	public Reactor(int nThreads, Consumer<X, R> consumer) {
		this(Executors.newFixedThreadPool(nThreads), nThreads * 2, consumer);
	}

	public Reactor(Executor executor, int maxPermits, Consumer<X, R> consumer) {
		super(executor, new ConcurrentLinkedQueue<X>(), maxPermits);
		this.consumer = consumer;
	}

	private final Consumer<X, R> consumer;
	private final Map<X, R> resultArea = new ConcurrentHashMap<X, R>();

	@Override
	protected final void process(X action) {
		R result = null;
		Exception cause = null;
		try {
			if (resultArea.containsKey(action)) {
				result = consumer.onSuccess(resultArea.remove(action), action);
			} else {
				result = consumer.apply(action);
			}
		} catch (Exception e) {
			cause = e;
		} finally {
			if (cause != null) {
				consumer.onFailure(action, cause);
			} else if (consumer.shouldReact(result)) {
				resultArea.put(action, result);
				accept(action);
			} else {
				consumer.onSuccess(result, action);
			}
		}
	}

	/**
	 * 
	 * Consumer
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	public static interface Consumer<X, R> {

		R apply(X action) throws Exception;

		default boolean shouldReact(R result) {
			return result != null;
		}

		default void onFailure(X action, Exception cause) {
			cause.printStackTrace();
		}

		default R onSuccess(R result, X action) {
			return null;
		}

	}

}
