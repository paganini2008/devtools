package com.github.paganini2008.devtools.cron4j;

import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * TaskExecutor
 *
 * @author Fred Feng
 * @version 1.0
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
	 * @version 1.0
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
