package com.github.paganini2008.devtools.cron4j;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Cancellables
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class Cancellables {

	public static Cancellable cancelIfRuns(int count) {
		final AtomicInteger total = new AtomicInteger(0);
		return taskDetail -> {
			return count > 0 && total.getAndIncrement() >= count;
		};
	}

	public static Cancellable cancelWhen(Date future) {
		return taskDetail -> {
			return System.currentTimeMillis() >= future.getTime();
		};
	}

}
