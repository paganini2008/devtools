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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;

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
	private final Hashtable<Task, TaskFuture> taskFutures = new Hashtable<Task, TaskFuture>();
	private TaskInterceptorHandler interceptorHandler = new TaskInterceptorHandler() {
	};

	public TimerTaskExecutor() {
		this(new Timer());
	}

	public TimerTaskExecutor(Timer timer) {
		this.timer = timer;
		this.running = true;
	}

	public TaskFuture schedule(Task task, long delay) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> {
			return System.currentTimeMillis() + delay;
		});
		taskDetail.nextExectionTime = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		timer.schedule(wrappedTask, delay);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
		return taskFutures.get(task);
	}

	public TaskFuture scheduleAtFixedRate(Task task, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> {
			return System.currentTimeMillis() + delay;
		});
		taskDetail.nextExectionTime = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		timer.scheduleAtFixedRate(wrappedTask, delay, period);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
		return taskFutures.get(task);
	}

	public TaskFuture scheduleWithFixedDelay(Task task, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> {
			return System.currentTimeMillis() + delay;
		});
		taskDetail.nextExectionTime = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		timer.schedule(wrappedTask, delay, period);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
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
			throw new IllegalStateException("All time are now past.");
		}
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> {
			return iterator.hasNext() ? ((CronExpression) iterator.next()).getTimeInMillis() : -1;
		});
		taskDetail.nextExectionTime = executed;
		final CronTask wrappedTask = new CronTask(task, taskDetail);
		timer.schedule(wrappedTask, executed - System.currentTimeMillis());
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
		return taskFutures.get(task);
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

	public boolean hasScheduled(Task task) {
		return taskFutures.containsKey(task);
	}

	public TaskFuture getTaskFuture(Task task) {
		return taskFutures.get(task);
	}

	/**
	 * 
	 * TaskFutureImpl
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
	 * @version 1.0
	 */
	class CronTask extends TimerTask {

		final Task task;
		final Cancellable cancellable;
		final DefaultTaskDetail taskDetail;

		CronTask(Task task, DefaultTaskDetail taskDetail) {
			this(task, task.cancellable(), taskDetail);
		}

		CronTask(Task task, Cancellable cancellable, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.cancellable = cancellable;
			this.taskDetail = taskDetail;
		}

		public void run() {
			if (taskDetail.nextExectionTime == -1) {
				removeSchedule(task);
				return;
			}
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = (TaskFutureImpl) taskFutures.get(task);
			Throwable throwing = null;
			taskDetail.running.set(true);
			try {
				taskDetail.lastExectionTime = now;
				taskDetail.nextExectionTime = taskDetail.trigger.getNextFiredTime();
				interceptorHandler.beforeJobExecution(taskFuture);
				if (!cancellable.cancel(taskDetail)) {
					result = (taskFuture != null && taskFuture.paused ? true : task.execute());
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
					CronTask nextTask = new CronTask(task, cancellable, taskDetail);
					timer.schedule(nextTask, taskDetail.nextExectionTime - System.currentTimeMillis());
					taskFuture.timerTask = nextTask;
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
	class SimpleTask extends TimerTask {

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
			final TaskFutureImpl taskFuture = (TaskFutureImpl) taskFutures.get(task);
			Throwable throwing = null;
			taskDetail.running.set(true);
			try {
				taskDetail.lastExectionTime = now;
				taskDetail.nextExectionTime = taskDetail.trigger.getNextFiredTime();
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
