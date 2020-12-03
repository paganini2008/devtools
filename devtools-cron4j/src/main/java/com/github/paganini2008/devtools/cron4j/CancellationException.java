package com.github.paganini2008.devtools.cron4j;

import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskDetail;

/**
 * 
 * CancellationException
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CancellationException extends SchedulingException {

	private static final long serialVersionUID = 5314985638545473194L;

	public CancellationException() {
		super();
		this.taskDetail = null;
	}

	public CancellationException(String msg) {
		super(msg);
		this.taskDetail = null;
	}

	public CancellationException(TaskDetail taskDetail) {
		super();
		this.taskDetail = taskDetail;
	}

	private final TaskDetail taskDetail;

	public TaskDetail getTaskDetail() {
		return taskDetail;
	}

}
