package com.github.paganini2008.devtools.scheduler;

import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskDetail;

/**
 * 
 * Condition for cancelling current task
 *
 * @author Fred Feng
 * @version 1.0
 */
@FunctionalInterface
public interface Cancellable {

	/**
	 * if true, cancel the task.
	 * 
	 * @param taskDetail
	 * @return
	 */
	boolean cancel(TaskDetail taskDetail);

}
