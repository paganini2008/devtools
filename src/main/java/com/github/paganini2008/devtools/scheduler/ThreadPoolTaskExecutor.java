package com.github.paganini2008.devtools.scheduler;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;
import com.github.paganini2008.devtools.scheduler.cron.CronUtils;

/**
 * 
 * ThreadPoolTaskExecutor
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2018-05
 * @version 1.0
 */
public class ThreadPoolTaskExecutor implements TaskExecutor {

	private final ScheduledExecutorService executor;
	private final ConcurrentMap<Executable, TaskFuture> taskFutures = new ConcurrentHashMap<Executable, TaskFuture>();

	public ThreadPoolTaskExecutor(int nThreads, String threadNamePrefix) {
		executor = Executors.newScheduledThreadPool(nThreads, new PooledThreadFactory(threadNamePrefix));
	}

	public ThreadPoolTaskExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public TaskFuture schedule(Executable e, long delay) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask task = new SimpleTask(e, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.schedule(task, delay, TimeUnit.MILLISECONDS);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(e);
	}

	public TaskFuture scheduleAtFixedRate(Executable e, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask task = new SimpleTask(e, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(e);
	}

	public TaskFuture scheduleWithFixedDelay(Executable e, long delay, long period) {
		final DefaultTaskDetail taskDetail = new DefaultTaskDetail(e, () -> System.currentTimeMillis() + delay);
		taskDetail.nextExecuted = System.currentTimeMillis() + delay;
		final SimpleTask task = new SimpleTask(e, taskDetail);
		ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, scheduledFuture));
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
		ScheduledFuture<?> scheduledFuture = executor.schedule(task, executed - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		taskFutures.put(e, new TaskFutureImpl(taskDetail, scheduledFuture));
		return taskFutures.get(e);
	}

	public void removeSchedule(Executable e) {
		TaskFuture taskFuture = taskFutures.remove(e);
		if (taskFuture != null) {
			taskFuture.cancel();
		}
	}

	public int taskCount() {
		return taskFutures.size();
	}

	public boolean isClosed() {
		return ExecutorUtils.isShutdown(executor);
	}

	public void close() {
		ExecutorUtils.gracefulShutdown(executor, 60000L);
	}

	/**
	 * 
	 * CronTask
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2018-05
	 * @version 1.0
	 */
	class CronTask implements Runnable {
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
			taskDetail.running.set(true);
			try {
				taskDetail.lastExecuted = now;
				taskDetail.nextExecuted = taskDetail.trigger.getNextFireTime();
				result = task.execute();
				taskDetail.completedCount.incrementAndGet();
			} catch (Exception e) {
				taskDetail.failedCount.incrementAndGet();
				result = task.onError(e);
			} finally {
				taskDetail.running.set(false);
				if (result) {
					removeSchedule(task);
					ScheduledFuture<?> scheduledFuture = executor.schedule(this, taskDetail.nextExecuted - System.currentTimeMillis(),
							TimeUnit.MILLISECONDS);
					taskFutures.put(task, new TaskFutureImpl(taskDetail, scheduledFuture));
				} else {
					removeSchedule(task);
					task.onCancellation();
				}
			}
		}
	}

	static class TaskFutureImpl implements TaskFuture {

		final TaskDetail taskDetail;
		volatile ScheduledFuture<?> scheduledFuture;

		TaskFutureImpl(TaskDetail taskDetail, ScheduledFuture<?> scheduledFuture) {
			this.taskDetail = taskDetail;
			this.scheduledFuture = scheduledFuture;
		}

		public boolean cancel() {
			return !scheduledFuture.isDone() ? scheduledFuture.cancel(false) : true;
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
	 * SimpleTask
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2018-05
	 * @version 1.0
	 */
	class SimpleTask implements Runnable {

		final Executable task;
		final DefaultTaskDetail taskDetail;

		SimpleTask(Executable task, DefaultTaskDetail taskDetail) {
			this.task = task;
			this.taskDetail = taskDetail;
		}

		public void run() {
			boolean result = false;
			final long now = System.currentTimeMillis();
			taskDetail.running.set(true);
			try {
				taskDetail.lastExecuted = now;
				taskDetail.nextExecuted = taskDetail.trigger.getNextFireTime();
				result = task.execute();
				taskDetail.completedCount.incrementAndGet();
			} catch (Exception e) {
				taskDetail.failedCount.incrementAndGet();
				result = task.onError(e);
			} finally {
				taskDetail.running.set(false);
				if (!result) {
					removeSchedule(task);
					task.onCancellation();
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		CronExpression expression = CronUtils.everySecond();
		ThreadPoolTaskExecutor es = new ThreadPoolTaskExecutor(2, null);
		final TaskFuture taskFuture = es.schedule(new Executable() {
			int n = 0;

			public boolean execute() {
				System.out.println("1111: " + DateUtils.format(System.currentTimeMillis()));
				return ++n < 10;
			}

			public void onCancellation() {
				System.out.println("取消: " + this.toString());
			}

		}, expression);

		System.in.read();
		es.close();
	}

}
