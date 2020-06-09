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

	ThatDayOfWeek day(int day);

	default ThatDayOfWeek Sun() {
		return day(Calendar.SUNDAY);
	}

	default ThatDayOfWeek Mon() {
		return day(Calendar.MONDAY);
	}

	default ThatDayOfWeek Tues() {
		return day(Calendar.TUESDAY);
	}

	default ThatDayOfWeek Wed() {
		return day(Calendar.WEDNESDAY);
	}

	default ThatDayOfWeek Thur() {
		return day(Calendar.THURSDAY);
	}

	default ThatDayOfWeek Fri() {
		return day(Calendar.FRIDAY);
	}

	default ThatDayOfWeek Sat() {
		return day(Calendar.SATURDAY);
	}

}
