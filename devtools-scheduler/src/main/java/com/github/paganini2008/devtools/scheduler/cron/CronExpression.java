package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executors;

import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.scheduler.Task;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskFuture;
import com.github.paganini2008.devtools.scheduler.ThreadPoolTaskExecutor;

/**
 * 
 * CronExpression
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface CronExpression extends Iterable<CronExpression> {

	Date getTime();

	long getTimeInMillis();

	default CronExpression copy() {
		return SerializationUtils.copy(this);
	}

	@SuppressWarnings("unchecked")
	default Iterator<CronExpression> iterator() {
		final CronExpression copy = copy();
		return (Iterator<CronExpression>) copy;
	}

	default TaskFuture test(Task task) {
		return new ThreadPoolTaskExecutor(Executors.newSingleThreadScheduledExecutor()).schedule(task, copy());
	}

}
