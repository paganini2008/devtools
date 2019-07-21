package com.github.paganini2008.devtools.scheduler;

import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;

/**
 * 
 * TaskExecutor
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2013-11
 * @version 1.0
 */
public interface TaskExecutor {

	TaskFuture schedule(Executable task, long delay);

	TaskFuture scheduleAtFixedRate(Executable task, long delay, long period);

	TaskFuture scheduleWithFixedDelay(Executable task, long delay, long period);

	TaskFuture schedule(Executable task, CronExpression cronExpression);

	default TaskFuture schedule(Executable task, long delay, TimeUnit timeUnit) {
		return schedule(task, DateUtils.convertToMillis(delay, timeUnit));
	}

	default TaskFuture scheduleAtFixedRate(Executable task, long period, TimeUnit timeUnit) {
		return scheduleAtFixedRate(task, period, period, timeUnit);
	}

	default TaskFuture scheduleAtFixedRate(Executable task, long delay, long period, TimeUnit timeUnit) {
		return scheduleAtFixedRate(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, timeUnit));
	}

	default TaskFuture scheduleAtFixedRate(Executable task, long delay, TimeUnit timeUnit, long period, TimeUnit periodTimeUnit) {
		return scheduleAtFixedRate(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, periodTimeUnit));
	}

	default TaskFuture scheduleWithFixedDelay(Executable task, long period, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(task, period, period, timeUnit);
	}

	default TaskFuture scheduleWithFixedDelay(Executable task, long delay, long period, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, timeUnit));
	}

	default TaskFuture scheduleWithFixedDelay(Executable task, long delay, TimeUnit timeUnit, long period, TimeUnit periodTimeUnit) {
		return scheduleWithFixedDelay(task, DateUtils.convertToMillis(delay, timeUnit), DateUtils.convertToMillis(period, periodTimeUnit));
	}

	void removeSchedule(Executable task);

	void close();

	boolean isClosed();

	int taskCount();

	/**
	 * 
	 * TaskDetail
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2013-11
	 * @version 1.0
	 */
	interface TaskDetail {

		boolean isRunning();

		int completedCount();

		int failedCount();

		long lastExecuted();

		long nextExecuted();

		Executable getTaskObject();

	}

	/**
	 * 
	 * TaskFuture
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2013-11
	 * @version 1.0
	 */
	interface TaskFuture {

		boolean cancel();

		boolean isCancelled();

		boolean isDone();

		TaskDetail getDetail();

	}

}
