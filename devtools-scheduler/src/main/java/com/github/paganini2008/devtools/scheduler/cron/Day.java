package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Day
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Day extends Iterator<Day>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getWeekDay();

	int getDayOfYear();

	default Hour everyHour() {
		return everyHour(1);
	}

	default Hour everyHour(int interval) {
		return everyHour(0, 23, interval);
	}

	default Hour everyHour(int from, int to, int interval) {
		return everyHour(d -> from, d -> to, interval);
	}

	default OneHour am() {
		return hour(0).toHour(12);
	}

	default OneHour pm() {
		return hour(12).toHour(23);
	}

	OneHour hour(int hour);

	Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval);

}
