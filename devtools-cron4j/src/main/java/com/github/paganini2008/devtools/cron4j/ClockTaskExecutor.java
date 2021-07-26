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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.multithreads.Clock;
import com.github.paganini2008.devtools.multithreads.ClockTask;

/**
 * 
 * ClockTaskExecutor
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class ClockTaskExecutor implements TaskExecutor {

	private final Clock clock;
	private final ConcurrentMap<Task, TaskFuture> taskFutures = new ConcurrentHashMap<Task, TaskFuture>();
	private TaskInterceptorHandler interceptorHandler = new TaskInterceptorHandler() {
	};

	public ClockTaskExecutor() {
		this(new Clock());
	}

	public ClockTaskExecutor(Clock clock) {
		this.clock = clock;
	}

	public TaskFuture schedule(Task task, long delay) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExectionTime = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		clock.schedule(wrappedTask, delay, TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
		return taskFutures.get(task);
	}

	public TaskFuture scheduleAtFixedRate(Task task, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExectionTime = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		clock.scheduleAtFixedRate(wrappedTask, delay, period, TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
		return taskFutures.get(task);
	}

	public TaskFuture scheduleWithFixedDelay(Task task, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExectionTime = System.currentTimeMillis() + delay;
		final SimpleTask wrappedTask = new SimpleTask(task, taskDetail);
		clock.schedule(wrappedTask, delay, period, TimeUnit.MILLISECONDS);
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
			throw new SchedulingException("All time are now past.");
		}
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(task, () -> {
			return iterator.hasNext() ? ((CronExpression) iterator.next()).getTimeInMillis() : -1;
		});
		taskDetail.nextExectionTime = executed;
		final CronTask wrappedTask = new CronTask(task, taskDetail);
		clock.schedule(wrappedTask, executed - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		taskFutures.put(task, new TaskFutureImpl(taskDetail, wrappedTask));
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
		return !clock.isRunning();
	}

	public void close() {
		for (TaskFuture taskFuture : taskFutures.values()) {
			taskFuture.cancel();
		}
		clock.stop();
	}

	/**
	 * 
	 * TaskFutureImpl
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class TaskFutureImpl implements TaskFuture {

		final DefaultTaskDetail taskDetail;
		volatile ClockTask clockTask;
		volatile boolean cancelled;
		volatile boolean done;
		volatile boolean paused;

		TaskFutureImpl(DefaultTaskDetail taskDetail, ClockTask clockTask) {
			this.taskDetail = taskDetail;
			this.clockTask = clockTask;
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
			return (done |= cancelled = clockTask.cancel());
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
	 * @since 2.0.1
	 */
	class CronTask extends ClockTask {

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

		public void runTask() {
			if (taskDetail.nextExectionTime == -1) {
				removeSchedule(task);
				return;
			}
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = ((TaskFutureImpl) taskFutures.get(task));
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
					clock.schedule(nextTask, taskDetail.nextExectionTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
					taskFuture.clockTask = nextTask;
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
	 * @since 2.0.1
	 */
	class SimpleTask extends ClockTask {

		final Task task;
		final Cancellable cancellable;
		final DefaultTaskDetail taskDetail;

		SimpleTask(Task task, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.cancellable = task.cancellable();
			this.taskDetail = taskDetail;
		}

		public void runTask() {
			boolean result = false;
			final long now = System.currentTimeMillis();
			final TaskFutureImpl taskFuture = ((TaskFutureImpl) taskFutures.get(task));
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
