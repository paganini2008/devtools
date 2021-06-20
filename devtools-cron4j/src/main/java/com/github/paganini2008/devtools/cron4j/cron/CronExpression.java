/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
public interface CronExpression extends CronStringBuilder {

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
	default void forEach(final Consumer<Date> consumer, final int n) {
		if (!(this instanceof Iterator)) {
			throw new UnsupportedOperationException();
		}
		final Date now = new Date();
		int i = 0;
		Date date;
		for (CronExpression cronExpression : CollectionUtils.forEach((Iterator<CronExpression>) copy())) {
			date = cronExpression.getTime();
			if (date.before(now)) {
				continue;
			}
			if (n < 0 || i++ < n) {
				consumer.accept(date);
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
