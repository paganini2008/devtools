package com.github.paganini2008.devtools.multithreads;

import java.util.UUID;

/**
 * 
 * ClockTask
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class ClockTask implements Runnable {

	private final String taskId;
	private volatile boolean cancelled;

	protected ClockTask() {
		this.taskId = UUID.randomUUID().toString();
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean cancel() {
		this.cancelled = true;
		return true;
	}

	public String getTaskId() {
		return taskId;
	}

	public void run() {
		if (!isCancelled()) {
			runTask();
		}
	}

	protected abstract void runTask();

}
