package com.github.paganini2008.devtools.cron4j;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;

/**
 * 
 * ThreadPoolTaskExecutor
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThreadPoolTaskExecutor implements TaskExecutor {

	private final ScheduledExecutorService executor;
	private final ConcurrentMap<Task, TaskFuture> taskFutures = new ConcurrentHashMap<Task, TaskFuture>();
	private TaskInterceptorHandler interceptorHandler = new TaskInterceptorHandler() {
	};

	public ThreadPoolTaskExecutor() {
		this(Executors.newSingleThreadScheduledExecutor());
	}

	public ThreadPoolTaskExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public TaskFuture schedule(Task task, long delay) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.schedule(wrappedTask, delay, TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(task);
	}

	public TaskFuture scheduleAtFixedRate(Task task, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(wrappedTask, delay, period, TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(task);
	}

	public TaskFuture scheduleWithFixedDelay(Task task, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(wrappedTask, delay, period, TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(task);
	}

	public TaskFuture schedule(Task task, CronExpression cronExpression) {
		final Iterator<?> iterator = (Iterator<?>) cronExpression;
		long executed = -1;
		while (iterator.hasNext()) {
			CronExpression cron = (CronExpression) iterator.next();
			if (cron.getTimeInMillis() > System.currentTimeMillis()) {
				executed = cron.getTimeInMillis();
				break;
			}
		}
		if (executed == -1) {
			throw new SchedulingException("All time are now past.");
		}
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> {
			return iterator.hasNext() ? ((CronExpression) iterator.next()).getTimeInMillis() : -1;
		});
		taskDetail.nextExecuted = executed;
		final CronTask wrappedTask = new CronTask(task, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.schedule(wrappedTask, executed - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(task);
	}

	public void setTaskInterceptorHandler(TaskInterceptorHandler interceptorHandler) {
		this.interceptorHandler = interceptorHandler;
	}

	public boolean hasScheduled(Task task) {
		return taskFutures.containsKey(task);
	}

	public void removeSchedule(Task task) {
		TaskFuture taskFuture = taskFutures.remove(task);
		if (taskFuture != null) {
			taskFuture.cancel();
		}
		if (taskFutures.isEmpty()) {
			close();
		}
	}

	public int taskCount() {
		return taskFutures.size();
	}

	public TaskFuture getTaskFuture(Task task) {
		return taskFutures.get(task);
	}

	public boolean isClosed() {
		return ExecutorUtils.isShutdown(executor);
	}

	public void close() {
		for (TaskFuture taskFuture : taskFutures.values()) {
			taskFuture.cancel();
		}
		ExecutorUtils.gracefulShutdown(executor, 60000L);
	}

	/**
	 * 
	 * TaskFutureImpl
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	static class TaskFutureImpl implements TaskFuture {

		final TaskDetail taskDetail;
		volatile ScheduledFuture<?> scheduledFuture;
		volatile boolean paused;

		TaskFutureImpl(TaskDetail taskDetail, ScheduledFuture<?> scheduledFuture) {
			this.taskDetail = taskDetail;
			this.scheduledFuture = scheduledFuture;
		}

		public void pause() {
			paused = true;
		}

		public void resume() {
			paused = false;
		}

		public boolean cancel() {
			return !scheduledFuture.isDone() ? scheduledFuture.cancel(false) : true;
		}

		public boolean isPaused() {
			return paused;
		}

		public boolean isCancelled() {
			return scheduledFuture.isCancelled();
		}

		public boolean isDone() {
			return scheduledFuture.isDone();
		}

		public TaskDetail getDetail() {
			return taskDetail;
		}

	}

	/**
	 * 
	 * CronTask
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	class CronTask implements Runnable {

		final Task task;
		final Cancellable cancellable;
		final DefaultTaskDetail taskDetail;

		CronTask(Task task, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.cancellable = task.cancellable();
			this.taskDetail = taskDetail;
		}

		public void run() {
			if (taskDetail.nextExecuted == -1) {
				removeSchedule(task);
				return;
			}
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = ((TaskFutureImpl) taskFutures.get(task));
			Throwable throwing = null;
			taskDetail.running.set(true);
			try {
				taskDetail.lastExecuted = now;
				taskDetail.nextExecuted = taskDetail.trigger.getNextFiredTime();
				interceptorHandler.beforeJobExecution(taskFuture);
				if (!cancellable.cancel(taskDetail)) {
					result = (taskFuture.paused ? true : task.execute());
					taskDetail.completedCount.incrementAndGet();
				} else {
					throw new CancellationException(taskDetail);
				}
			} catch (Throwable e) {
				throwing = e;
				if (!(e instanceof CancellationException)) {
					taskDetail.failedCount.incrementAndGet();
					result = task.onError(e);
				}
			} finally {
				taskDetail.running.set(false);
				interceptorHandler.afterJobExecution(taskFuture, throwing);

				if (result) {
					ScheduledFuture<?> scheduledFuture = executor.schedule(this, taskDetail.nextExecuted - System.currentTimeMillis(),
							TimeUnit.MILLISECONDS);
					taskFuture.scheduledFuture = scheduledFuture;
				} else {
					removeSchedule(task);
					task.onCancellation(throwing);
				}
			}
		}
	}

	/**
	 * 
	 * SimpleTask
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	class SimpleTask implements Runnable {

		final Task task;
		final Cancellable cancellable;
		final DefaultTaskDetail taskDetail;

		SimpleTask(Task task, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.cancellable = task.cancellable();
			this.taskDetail = taskDetail;
		}

		public void run() {
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = ((TaskFutureImpl) taskFutures.get(task));
			Throwable throwing = null;
			taskDetail.running.set(true);
			try {
				taskDetail.lastExecuted = now;
				taskDetail.nextExecuted = taskDetail.trigger.getNextFiredTime();
				interceptorHandler.beforeJobExecution(taskFuture);
				if (!cancellable.cancel(taskDetail)) {
					result = (taskFuture.paused ? true : task.execute());
					taskDetail.completedCount.incrementAndGet();
				} else {
					throw new CancellationException(taskDetail);
				}
			} catch (Throwable e) {
				throwing = e;
				if (!(e instanceof CancellationException)) {
					taskDetail.failedCount.incrementAndGet();
					result = task.onError(e);
				}
			} finally {
				taskDetail.running.set(false);
				interceptorHandler.afterJobExecution(taskFuture, throwing);

				if (!result) {
					removeSchedule(task);
					task.onCancellation(throwing);
				}
			}
		}

	}

}
