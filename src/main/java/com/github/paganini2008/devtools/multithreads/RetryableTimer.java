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

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * 
 * RetryableTimer
 * <p>
 * A {@link ConcurrentTimer} that executes a {@link Retryable} immediately and, if it fails with a
 * captured exception, retries it periodically until it succeeds or the maximum number of retries
 * is exceeded.
 *
 * @author Fred Feng
 * @date 2021-08-13
 *
 * @since 2.0.3
 */
public class RetryableTimer extends ConcurrentTimer {

	private final Map<Retryable, Timer> currentRetryables = new ConcurrentHashMap<Retryable, Timer>();

	/**
	 * Executes the retryable now; if it fails with a captured exception it is retried at a fixed
	 * rate until it succeeds or {@link Retryable#getMaxRetries()} is exceeded. A retryable that is
	 * already being retried is not scheduled again.
	 *
	 * @param retryable
	 *            the task
	 * @param interval
	 *            the retry interval
	 * @param timeUnit
	 *            the unit of {@code interval}
	 */
	public void executeAndRetryAtFixedRate(Retryable retryable, long interval, TimeUnit timeUnit) {
		try {
			retryable.execute();
		} catch (Throwable e) {
			if (ArrayUtils.isNotEmpty(retryable.captureClasses()) && ClassUtils.contains(retryable.captureClasses(), e.getClass())) {
				synchronized (RetryableTimer.this) {
					if (!currentRetryables.containsKey(retryable)) {
						Timer timer = super.scheduleAtFixedRate(new RetryableExecutable(retryable), interval, timeUnit);
						currentRetryables.put(retryable, timer);
					}
				}
			} else {
				retryable.onError(0, e);
			}
		}

	}

	/**
	 * Same as {@link #executeAndRetryAtFixedRate(Retryable, long, TimeUnit)} but retries with a
	 * fixed delay between attempts.
	 *
	 * @param retryable
	 *            the task
	 * @param interval
	 *            the delay between attempts
	 * @param timeUnit
	 *            the unit of {@code interval}
	 */
	public void executeAndRetryWithFixedDelay(Retryable retryable, long interval, TimeUnit timeUnit) {
		try {
			retryable.execute();
		} catch (Throwable e) {
			if (ArrayUtils.isNotEmpty(retryable.captureClasses()) && ClassUtils.contains(retryable.captureClasses(), e.getClass())) {
				synchronized (RetryableTimer.this) {
					if (!currentRetryables.containsKey(retryable)) {
						Timer timer = super.scheduleWithFixedDelay(new RetryableExecutable(retryable), interval, timeUnit);
						currentRetryables.put(retryable, timer);
					}
				}
			} else {
				retryable.onError(0, e);
			}
		}

	}

	@Override
	public void cancel() {
		super.cancel();
		synchronized (RetryableTimer.this) {
			currentRetryables.clear();
		}
	}

	/**
	 * 
	 * RetryableExecutable
	 *
	 * @author Fred Feng
	 *
	 * @since 2.0.3
	 */
	class RetryableExecutable implements Executable {

		private final Retryable retryable;
		private final AtomicInteger counter = new AtomicInteger(0);

		RetryableExecutable(Retryable retryable) {
			this.retryable = retryable;
		}

		@Override
		public boolean execute() throws Throwable {
			retryable.execute();
			return false;
		}

		@Override
		public boolean onError(Throwable e) {
			int count = 1;
			try {
				if ((count = counter.incrementAndGet()) <= retryable.getMaxRetries()) {
					if (ArrayUtils.isNotEmpty(retryable.captureClasses())
							&& ClassUtils.contains(retryable.captureClasses(), e.getClass())) {
						return true;
					}
				}
				return false;
			} finally {
				retryable.onError(count, e);
			}
		}

		@Override
		public void onCancellation(Throwable e) {
			// The last error has already been reported through onError(Throwable)
			synchronized (RetryableTimer.this) {
				currentRetryables.remove(retryable);
			}
		}

	}

}
