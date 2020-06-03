package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Week
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Week extends Iterator<Week>, CronExpression {

	int getYear();

	int getMonth();

	int getWeek();

	int getWeekOfYear();

	default Day everyDay() {
		return everyDay(1);
	}

	default Day everyDay(int interval) {
		return everyDay(Calendar.SUNDAY, Calendar.SATURDAY, interval);
	}

	default Day everyDay(int from, int to, int interval) {
		return everyDay(w -> from, w -> to, interval);
	}

	Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval);

	OneWeekDay weekday(int day);

	default OneWeekDay Sun() {
		return weekday(Calendar.SUNDAY);
	}

	default OneWeekDay Mon() {
		return weekday(Calendar.MONDAY);
	}

	default OneWeekDay Tues() {
		return weekday(Calendar.TUESDAY);
	}

	default OneWeekDay Wed() {
		return weekday(Calendar.WEDNESDAY);
	}

	default OneWeekDay Thur() {
		return weekday(Calendar.THURSDAY);
	}

	default OneWeekDay Fri() {
		return weekday(Calendar.FRIDAY);
	}

	default OneWeekDay Sat() {
		return weekday(Calendar.SATURDAY);
	}

}
