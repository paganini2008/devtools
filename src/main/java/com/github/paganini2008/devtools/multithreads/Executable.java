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
 * Executable
 * <p>
 * A repeatable task run by the timers of {@link ThreadUtils} and {@link ConcurrentTimer}; it keeps
 * being scheduled while {@link #execute()} returns true.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Executable {

	/**
	 * Runs one execution.
	 *
	 * @return true to keep the task scheduled, false to cancel it
	 * @throws Throwable
	 *            any failure; {@link #onError(Throwable)} then decides whether to continue
	 */
	boolean execute() throws Throwable;

	/**
	 * Handles a failure of {@link #execute()}. The default prints the stack trace and cancels the
	 * task.
	 *
	 * @param e
	 *            the failure
	 * @return true to keep the task scheduled
	 */
	default boolean onError(Throwable e) {
		e.printStackTrace();
		return false;
	}

	/**
	 * Called once when the task is cancelled.
	 *
	 * @param e
	 *            the failure that caused the cancellation, or null if it was cancelled normally
	 */
	default void onCancellation(Throwable e) {
	}

}
