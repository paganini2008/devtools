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
 * Promise
 * <p>
 * The pending result of an {@link Action} submitted to a {@link ThreadPool}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Promise<R> {

	/**
	 * Returns whether {@link #cancel()} has been called before completion.
	 *
	 * @return true if cancelled
	 */
	boolean isCancelled();

	/**
	 * Returns whether the action has completed or the promise was cancelled.
	 *
	 * @return true if completed or cancelled
	 */
	boolean isDone();

	/**
	 * Returns the milliseconds elapsed since submission.
	 *
	 * @return the elapsed milliseconds
	 */
	long getElapsed();

	/**
	 * Waits until the action completes (or the promise is cancelled, or the waiting thread is
	 * interrupted) and returns its result.
	 *
	 * @return the result, or null if not available
	 */
	R get();

	/**
	 * Waits at most {@code timeout} milliseconds for the result.
	 *
	 * @param timeout
	 *            the maximum milliseconds to wait
	 * @return the result, or null if it is not available yet
	 */
	R get(long timeout);

	/**
	 * Cancels the promise: waiting threads return immediately. The action itself is not
	 * interrupted.
	 */
	void cancel();

}
