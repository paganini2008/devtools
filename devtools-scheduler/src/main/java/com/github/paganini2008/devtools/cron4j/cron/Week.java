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

	OneDayOfWeek weekday(int day);

	default OneDayOfWeek Sun() {
		return weekday(Calendar.SUNDAY);
	}

	default OneDayOfWeek Mon() {
		return weekday(Calendar.MONDAY);
	}

	default OneDayOfWeek Tues() {
		return weekday(Calendar.TUESDAY);
	}

	default OneDayOfWeek Wed() {
		return weekday(Calendar.WEDNESDAY);
	}

	default OneDayOfWeek Thur() {
		return weekday(Calendar.THURSDAY);
	}

	default OneDayOfWeek Fri() {
		return weekday(Calendar.FRIDAY);
	}

	default OneDayOfWeek Sat() {
		return weekday(Calendar.SATURDAY);
	}

}
