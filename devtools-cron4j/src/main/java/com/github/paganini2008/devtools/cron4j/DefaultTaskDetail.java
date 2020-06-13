package com.github.paganini2008.devtools.cron4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskDetail;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * DefaultTaskDetail
 *
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultTaskDetail implements TaskDetail {

	final Task task;
	final Trigger trigger;
	final AtomicBoolean running = new AtomicBoolean(false);
	final AtomicInteger completedCount = new AtomicInteger(0);
	final AtomicInteger failedCount = new AtomicInteger(0);
	volatile long lastExecuted = -1;
	volatile long nextExecuted = -1;

	DefaultTaskDetail(Task task, Trigger trigger) {
		this.task = task;
		this.trigger = trigger;
	}

	public boolean isRunning() {
		return running.get();
	}

	public int completedCount() {
		return completedCount.get();
	}

	public int failedCount() {
		return failedCount.get();
	}

	public long lastExecuted() {
		return lastExecuted;
	}

	public long nextExecuted() {
		return nextExecuted;
	}

	public void completedCount(int count) {
		completedCount.set(count);
	}

	public void failedCount(int count) {
		failedCount.set(count);
	}

	public void nextExecuted(long time) {
		nextExecuted = time;
	}

	public Task getTaskObject() {
		return task;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[TaskDetail] Task: ").append(task.getClass().getName());
		str.append(", Running: ").append(isRunning());
		str.append(", CompletedCount: ").append(completedCount());
		str.append(", FailedCount: ").append(failedCount());
		str.append(", LastExecuted: ").append(lastExecuted() > 0 ? DateUtils.format(lastExecuted()) : "-");
		str.append(", NextExecuted: ").append(nextExecuted() > 0 ? DateUtils.format(nextExecuted()) : "-");
		return str.toString();
	}

}