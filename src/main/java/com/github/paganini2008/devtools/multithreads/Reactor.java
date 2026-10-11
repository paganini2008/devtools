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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * Reactor
 * <p>
 * A {@link ForEach} that feeds each element to a {@link Consumer}. While
 * {@link Consumer#shouldReact(Object)} accepts the latest result, the consumer's
 * {@link Consumer#onSuccess(Object, Object)} is invoked again with it to produce the next result;
 * the chain runs in the same worker thread.
 *
 * @author Fred Feng
 * @date 2020-05-04
 * @since 2.0.1
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

	@Override
	protected final void process(X action) {
		R result;
		try {
			result = consumer.apply(action);
			while (consumer.shouldReact(result)) {
				result = consumer.onSuccess(result, action);
			}
		} catch (Exception e) {
			consumer.onFailure(action, e);
			return;
		}
		consumer.onSuccess(result, action);
	}

	/**
	 * 
	 * Consumer
	 * <p>
	 * Callback interface of {@link Reactor}.
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static interface Consumer<X, R> {

		/**
		 * Processes an element.
		 * 
		 * @param action
		 *            the element
		 * @return the first result
		 * @throws Exception
		 *             if processing fails; {@link #onFailure(Object, Exception)} is then called
		 */
		R apply(X action) throws Exception;

		/**
		 * Decides whether a result should be fed back to {@link #onSuccess(Object, Object)} to
		 * produce a further result. The default reacts to every non-null result.
		 * 
		 * @param result
		 *            the latest result
		 * @return true to keep reacting
		 */
		default boolean shouldReact(R result) {
			return result != null;
		}

		/**
		 * Called when {@link #apply(Object)} or a reaction throws.
		 * 
		 * @param action
		 *            the element
		 * @param cause
		 *            the exception
		 */
		default void onFailure(X action, Exception cause) {
			cause.printStackTrace();
		}

		/**
		 * Reacts to a result. While {@link #shouldReact(Object)} returns true the returned value
		 * is the next result; finally it is called once more with the last result (whose return
		 * value is ignored).
		 * 
		 * @param result
		 *            the latest result
		 * @param action
		 *            the element
		 * @return the next result
		 */
		default R onSuccess(R result, X action) {
			return null;
		}

	}

}
