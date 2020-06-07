package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Day
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Day extends Iterator<Day>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getDayOfWeek();

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

	OneHour hour(int hourOfDay);

	default OneMinute at(int hourOfDay, int minute) {
		return hour(hourOfDay).minute(minute);
	}

	default OneSecond at(int hourOfDay, int minute, int second) {
		return hour(hourOfDay).minute(minute).second(second);
	}

	Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval);

}
