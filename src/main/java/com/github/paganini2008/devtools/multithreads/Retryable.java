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

/**
 * 
 * Retryable
 * <p>
 * A task executed by {@link RetryableTimer} that is retried on failure.
 *
 * @author Fred Feng
 * @date 2019-07-24
 *
 * @since 2.0.3
 */
public interface Retryable {

	/**
	 * Executes the task.
	 *
	 * @throws Throwable
	 *            any failure; captured failures trigger a retry
	 */
	void execute() throws Throwable;

	/**
	 * Returns the maximum number of retries (not counting the first attempt).
	 *
	 * @return the maximum retries, unlimited by default
	 */
	default int getMaxRetries() {
		return Integer.MAX_VALUE;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Returns the exception types that trigger a retry (subclasses included).
	 *
	 * @return the captured exception types, {@code Exception} by default
	 */
	default Class<? extends Exception>[] captureClasses() {
		return new Class[] { Exception.class };
	}

	/**
	 * Called when an attempt fails: with {@code count == 0} for a non-retryable failure of the first
	 * attempt, otherwise with the number of the failed retry.
	 *
	 * @param count
	 *            the retry number
	 * @param e
	 *            the failure
	 */
	default void onError(int count, Throwable e) {
	}

}
