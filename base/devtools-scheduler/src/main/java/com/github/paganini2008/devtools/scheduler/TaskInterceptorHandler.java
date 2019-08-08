package com.github.paganini2008.devtools.scheduler;

import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskFuture;

/**
 * 
 * TaskInterceptorHandler
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public interface TaskInterceptorHandler {

	default void beforeJobExecution(TaskFuture future) {
	}

	default void afterJobExecution(TaskFuture future) {
	}

}
