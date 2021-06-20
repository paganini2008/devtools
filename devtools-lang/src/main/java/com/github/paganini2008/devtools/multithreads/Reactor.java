/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
