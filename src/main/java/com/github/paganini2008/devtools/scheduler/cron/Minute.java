package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Minute
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Minute extends Iterator<Minute>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getHour();

	int getMinute();

	default Second everySecond(int interval) {
		return everySecond(0, 59, interval);
	}

	default Second everySecond(int from, int to, int interval) {
		return everySecond(m -> from, m -> to, interval);
	}
	
	ConcreteSecond second(int second);

	Second everySecond(Function<Minute, Integer> from, Function<Minute, Integer> to, int interval);

}
