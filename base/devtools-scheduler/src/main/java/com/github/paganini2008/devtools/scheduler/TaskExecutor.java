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

	void setTaskInterceptorHandler(TaskInterceptorHandler interceptorHandler);

	void removeSchedule(Executable task);

	boolean hasScheduled(Executable task);

	TaskFuture getTaskFuture(Executable task);

	int taskCount();

	void close();

	boolean isClosed();

	/**
	 * 
	 * TaskDetail
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2013-11
	 * @version 1.0
	 */
	public interface TaskDetail {

		boolean isRunning();

		int completedCount();

		int failedCount();

		long lastExecuted();

		long nextExecuted();

		void completedCount(int count);

		void failedCount(int count);

		void nextExecuted(long time);

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
