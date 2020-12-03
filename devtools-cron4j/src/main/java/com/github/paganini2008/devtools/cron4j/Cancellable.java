package com.github.paganini2008.devtools.cron4j;

import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskDetail;

/**
 * 
 * Condition for cancelling current task
 *
 * @author Jimmy Hoff
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
