package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.Task;
import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskFuture;
import com.github.paganini2008.devtools.cron4j.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.io.SerializationUtils;

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

	CronExpression getParent();

	default String toCronString() {
		throw new UnsupportedOperationException();
	}

	/**
	 * copy this
	 * 
	 * @return
	 */
	default CronExpression copy() {
		return SerializationUtils.copy(this);
	}

	@SuppressWarnings("unchecked")
	default void forEach(final Consumer<Date> consumer, final int n) {
		if (!(this instanceof Iterator)) {
			throw new UnsupportedOperationException();
		}
		int i = 0;
		for (CronExpression cronExpression : CollectionUtils.forEach((Iterator<CronExpression>) copy())) {
			if (n < 0 || i++ < n) {
				consumer.accept(cronExpression.getTime());
			} else {
				break;
			}
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
