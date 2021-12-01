/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.cron4j;

import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * TaskExecutor
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface TaskExecutor {

	TaskFuture schedule(Task task, long delay);

	TaskFuture scheduleAtFixedRate(Task task, long delay, long period);

	TaskFuture scheduleWithFixedDelay(Task task, long delay, long period);

	TaskFuture schedule(Task task, CronExpression cronExpression);

	default TaskFuture schedule(Task task, long delay, TimeUnit timeUnit) {
		return schedule(task, DateUtils.convertToMillis(delay, timeUnit));
	}

	default TaskFuture scheduleAtFixedRate(Task task, long period, TimeUnit timeUnit) {
		return scheduleAtFixedRate(task, period, period, timeUnit);
	}

	default TaskFuture scheduleAtFixedRate(Task task, long delay, long period, TimeUnit timeUnit) {
		return scheduleAtFixedRate(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, timeUnit));
	}

	default TaskFuture scheduleAtFixedRate(Task task, long delay, TimeUnit timeUnit, long period, TimeUnit periodTimeUnit) {
		return scheduleAtFixedRate(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, periodTimeUnit));
	}

	default TaskFuture scheduleWithFixedDelay(Task task, long period, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(task, period, period, timeUnit);
	}

	default TaskFuture scheduleWithFixedDelay(Task task, long delay, long period, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, timeUnit));
	}

	default TaskFuture scheduleWithFixedDelay(Task task, long delay, TimeUnit timeUnit, long period, TimeUnit periodTimeUnit) {
		return scheduleWithFixedDelay(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, periodTimeUnit));
	}

	void setTaskInterceptorHandler(TaskInterceptorHandler interceptorHandler);

	void removeSchedule(Task task);

	boolean hasScheduled(Task task);

	TaskFuture getTaskFuture(Task task);

	int taskCount();

	void close();

	boolean isClosed();

	/**
	 * 
	 * TaskDetail
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public interface TaskDetail {

		boolean isRunning();

		int completedCount();

		int failedCount();

		long lastExectionTime();

		long nextExectionTime();

		void completedCount(int count);

		void failedCount(int count);

		void nextExectionTime(long time);

		Task getTaskObject();

	}

	/**
	 * 
	 * TaskFuture
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public interface TaskFuture {

		void pause();

		void resume();

		boolean cancel();

		boolean isCancelled();

		boolean isDone();
		
		boolean isPaused();

		TaskDetail getDetail();

	}

}
