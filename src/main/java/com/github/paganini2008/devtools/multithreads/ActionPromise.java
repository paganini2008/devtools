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

import java.util.concurrent.TimeUnit;

/**
 * 
 * ActionPromise
 * <p>
 * Runs an {@link Action} (including its chain of reactions) and exposes the outcome as a
 * {@link Promise}. The value of the promise is the last non-null result produced by the action
 * or its reactions; it is {@code null} if the action failed before producing one.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
final class ActionPromise<R> implements Promise<R>, Runnable {

	private final Action<R> action;
	private final ThreadPool threadPool;
	private final long startTime;
	private final Object monitor = new Object();
	private R value;
	private volatile boolean done;
	private volatile boolean cancelled;

	ActionPromise(Action<R> action, ThreadPool threadPool) {
		this.action = action;
		this.threadPool = threadPool;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		R last = null;
		try {
			R result = action.execute();
			while (true) {
				if (result != null) {
					last = result;
				}
				if (!action.shouldReact(result)) {
					break;
				}
				result = action.onReaction(result, threadPool);
			}
		} catch (Exception e) {
			action.onFailure(e, threadPool);
		} finally {
			synchronized (monitor) {
				value = last;
				done = true;
				monitor.notifyAll();
			}
		}
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return done || cancelled;
	}

	@Override
	public long getElapsed() {
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public R get() {
		synchronized (monitor) {
			while (!isDone()) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
			return value;
		}
	}

	@Override
	public R get(long timeout) {
		long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
		final long deadline = System.nanoTime() + nanos;
		synchronized (monitor) {
			while (!isDone() && nanos > 0) {
				try {
					TimeUnit.NANOSECONDS.timedWait(monitor, nanos);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
				nanos = deadline - System.nanoTime();
			}
			return value;
		}
	}

	@Override
	public void cancel() {
		synchronized (monitor) {
			if (!done) {
				cancelled = true;
				monitor.notifyAll();
			}
		}
	}

}
