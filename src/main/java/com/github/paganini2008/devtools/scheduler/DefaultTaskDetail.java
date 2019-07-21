package com.github.paganini2008.devtools.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskDetail;

/**
 * 
 * DefaultTaskDetail
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2013-11
 * @version 1.0
 */
public class DefaultTaskDetail implements TaskDetail {

	final Executable task;
	final Trigger trigger;
	final AtomicBoolean running = new AtomicBoolean(false);
	final AtomicInteger completedCount = new AtomicInteger(0);
	final AtomicInteger failedCount = new AtomicInteger(0);
	volatile long lastExecuted = -1;
	volatile long nextExecuted = -1;

	DefaultTaskDetail(Executable task, Trigger trigger) {
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

	public Executable getTaskObject() {
		return task;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[TaskExecutor] Task: ").append(task.getClass().getName());
		str.append(", Running: ").append(isRunning());
		str.append(", CompletedCount: ").append(completedCount());
		str.append(", FailedCount: ").append(failedCount());
		str.append(", LastExecuted: ").append(lastExecuted() > 0 ? DateUtils.format(lastExecuted()) : "-");
		str.append(", NextExecuted: ").append(nextExecuted() > 0 ? DateUtils.format(nextExecuted()) : "-");
		return str.toString();
	}

}