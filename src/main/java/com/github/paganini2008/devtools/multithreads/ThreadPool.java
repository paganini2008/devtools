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

import java.util.concurrent.Executor;

/**
 * 
 * ThreadPool
 * <p>
 * A thread pool abstraction supporting permit-limited execution, a bounded wait queue and
 * {@link Action}/{@link Promise} style submission.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface ThreadPool extends Executor {

	/**
	 * Same as {@link #apply(Runnable)}, ignoring the result.
	 */
	default void execute(Runnable task) {
		apply(task);
	}

	/**
	 * Runs the task if possible, otherwise parks it in the wait queue.
	 *
	 * @param task
	 *            the task
	 * @return true if the task was handed to a worker immediately
	 */
	boolean apply(Runnable task);

	/**
	 * Submits an action.
	 *
	 * @param action
	 *            the action
	 * @return the promise of its result
	 */
	<R> Promise<R> submit(Action<R> action);

	/**
	 * Returns the current number of worker threads.
	 *
	 * @return the pool size
	 */
	int getPoolSize();

	/**
	 * Returns the maximum number of worker threads.
	 *
	 * @return the maximum pool size
	 */
	int getMaxPoolSize();

	/**
	 * Returns the number of parked tasks.
	 *
	 * @return the wait queue size
	 */
	int getQueueSize();

	/**
	 * Returns the number of threads running tasks.
	 *
	 * @return the number of busy threads
	 */
	int getActiveThreadSize();

	/**
	 * Returns the number of idle threads.
	 *
	 * @return the number of idle threads
	 */
	int getIdleThreadSize();

	/**
	 * Returns the number of completed tasks (including failed ones).
	 *
	 * @return the completed task count
	 */
	long getCompletedTaskCount();

	/**
	 * Returns the number of tasks that threw an exception.
	 *
	 * @return the failed task count
	 */
	long getFailedTaskCount();

	/**
	 * Returns whether the pool has been shut down.
	 *
	 * @return true if shut down
	 */
	boolean isShutdown();

	/**
	 * Waits for all running and parked tasks to complete, then shuts the pool down.
	 */
	void shutdown();

	/**
	 * Sets the handler for tasks that can be neither executed nor parked. Without a handler such
	 * tasks cause an {@link IllegalStateException}.
	 *
	 * @param rejectedExecutionHandler
	 *            the handler, may be null
	 */
	void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);

}
