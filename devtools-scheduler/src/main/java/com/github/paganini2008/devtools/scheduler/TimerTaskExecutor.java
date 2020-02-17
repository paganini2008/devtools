package com.github.paganini2008.devtools.scheduler;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;

/**
 * 
 * TimerTaskExecutor
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TimerTaskExecutor implements TaskExecutor {

	private volatile boolean running;
	private final Timer timer;
	private final Hashtable<Executable, TaskFuture> taskFutures = new Hashtable<Executable, TaskFuture>();
	private TaskInterceptorHandler interceptorHandler = new TaskInterceptorHandler() {
	};

	public TimerTaskExecutor() {
		this(new Timer());
	}

	public TimerTaskExecutor(Timer timer) {
		this.timer = timer;
		this.running = true;
	}

	public TaskFuture schedule(Executable e, long delay) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> {
			return System.currentTimeMillis() + delay;
		});
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask task = new SimpleTask(e, taskDetail);
		timer.schedule(task, delay);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, task));
		return taskFutures.get(e);
	}

	public TaskFuture scheduleAtFixedRate(Executable e, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> {
			return System.currentTimeMillis() + delay;
		});
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask task = new SimpleTask(e, taskDetail);
		timer.scheduleAtFixedRate(task, delay, period);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, task));
		return taskFutures.get(e);
	}

	public TaskFuture scheduleWithFixedDelay(Executable e, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> {
			return System.currentTimeMillis() + delay;
		});
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask task = new SimpleTask(e, taskDetail);
		timer.schedule(task, delay, period);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, task));
		return taskFutures.get(e);
	}

	public TaskFuture schedule(Executable e, CronExpression cronExpression) {
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
			throw new IllegalStateException("All time are now past.");
		}
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> {
			return iterator.hasNext() ? ((CronExpression) iterator.next()).getTimeInMillis() : -1;
		});
		taskDetail.nextExecuted = executed;
		final CronTask task = new CronTask(e, taskDetail);
		timer.schedule(task, executed - System.currentTimeMillis());
		taskFutures.put(e, new TaskFutureImpl(taskDetail, task));
		return taskFutures.get(e);
	}

	public void removeSchedule(Executable task) {
		TaskFuture taskFuture = taskFutures.remove(task);
		if (taskFuture != null) {
			taskFuture.cancel();
		}
	}

	public void close() {
		timer.cancel();
		running = false;
	}

	public boolean isClosed() {
		return !running;
	}

	public int taskCount() {
		return taskFutures.size();
	}

	public void setTaskInterceptorHandler(TaskInterceptorHandler interceptorHandler) {
		this.interceptorHandler = interceptorHandler;
	}

	public boolean hasScheduled(Executable task) {
		return taskFutures.containsKey(task);
	}

	public TaskFuture getTaskFuture(Executable task) {
		return taskFutures.get(task);
	}

	/**
	 * 
	 * CronTask
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	static class TaskFutureImpl implements TaskFuture {

		final DefaultTaskDetail taskDetail;
		volatile TimerTask timerTask;
		volatile boolean cancelled;
		volatile boolean done;
		volatile boolean paused;

		TaskFutureImpl(DefaultTaskDetail taskDetail, TimerTask timerTask) {
			this.timerTask = timerTask;
			this.taskDetail = taskDetail;
		}

		public void pause() {
			paused = true;
		}

		public void resume() {
			paused = false;
		}

		public boolean isPaused() {
			return paused;
		}

		public boolean cancel() {
			return (done |= cancelled = timerTask.cancel());
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public boolean isDone() {
			return done;
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
	 * 
	 * 
	 * @version 1.0
	 */
	class CronTask extends TimerTask {

		final Executable task;
		final DefaultTaskDetail taskDetail;

		CronTask(Executable task, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.taskDetail = taskDetail;
		}

		public void run() {
			if (taskDetail.nextExecuted == -1) {
				removeSchedule(task);
				return;
			}
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = (TaskFutureImpl) taskFutures.get(task);
			taskDetail.running.set(true);
			try {
				taskDetail.lastExecuted = now;
				taskDetail.nextExecuted = taskDetail.trigger.getNextFireTime();
				interceptorHandler.beforeJobExecution(taskFuture);
				result = task.execute();
				taskDetail.completedCount.incrementAndGet();
			} catch (Exception e) {
				taskDetail.failedCount.incrementAndGet();
				result = task.onError(e);
			} finally {
				taskDetail.running.set(false);
				interceptorHandler.afterJobExecution(taskFuture);
				if (result) {
					CronTask nextTask = new CronTask(task, taskDetail);
					timer.schedule(nextTask, taskDetail.nextExecuted - System.currentTimeMillis());
					taskFuture.timerTask = nextTask;
				} else {
					removeSchedule(task);
					task.onCancellation();
				}
			}
		}

	}

	/**
	 * 
	 * SimpleTask
	 *
	 * @author Fred Feng
	 * 
	 * 
	 * @version 1.0
	 */
	class SimpleTask extends TimerTask {

		final Executable task;
		final DefaultTaskDetail taskDetail;

		SimpleTask(Executable task, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.taskDetail = taskDetail;
		}

		public void run() {
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = (TaskFutureImpl) taskFutures.get(task);
			taskDetail.running.set(true);
			try {
				taskDetail.lastExecuted = now;
				taskDetail.nextExecuted = taskDetail.trigger.getNextFireTime();
				interceptorHandler.beforeJobExecution(taskFuture);
				result = task.execute();
				taskDetail.completedCount.incrementAndGet();
			} catch (Exception e) {
				taskDetail.failedCount.incrementAndGet();
				result = task.onError(e);
			} finally {
				taskDetail.running.set(false);
				interceptorHandler.afterJobExecution(taskFuture);
				if (!result) {
					removeSchedule(task);
					task.onCancellation();
				}
			}
		}

	}

}
