package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.github.paganini2008.devtools.collection.CollectionUtils;
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
public interface CronExpression {

	Date getTime();

	long getTimeInMillis();

	/**
	 * copy this
	 * 
	 * @return
	 */
	default CronExpression copy() {
		return SerializationUtils.copy(this);
	}

	@SuppressWarnings("unchecked")
	default void forEach(Consumer<Date> consumer) {
		if (!(this instanceof Iterator)) {
			throw new UnsupportedOperationException();
		}
		for (CronExpression cronExpression : CollectionUtils.forEach((Iterator<CronExpression>) copy())) {
			consumer.accept(cronExpression.getTime());
		}
	}

	/**
	 * Just for testing
	 * 
	 * @param task
	 * @return
	 */
	default TaskFuture test(Task task) {
		return new ThreadPoolTaskExecutor(Executors.newSingleThreadScheduledExecutor()).schedule(task, copy());
	}

}
