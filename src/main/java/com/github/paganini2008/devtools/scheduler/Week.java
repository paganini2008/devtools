package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Week
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Week extends Iterator<Week>, CurrentTime {

	int getYear();

	int getMonth();

	int getWeek();

	int getWeekOfYear();

	default Day everyDay(int interval) {
		return everyDay(Calendar.SUNDAY, Calendar.SATURDAY, interval);
	}

	default Day everyDay(int from, int to, int interval) {
		return everyDay(w -> from, w -> to, interval);
	}

	Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval);

	ConcreteWeekDay weekday(int day);

	default ConcreteWeekDay Sun() {
		return weekday(Calendar.SUNDAY);
	}

	default ConcreteWeekDay Mon() {
		return weekday(Calendar.MONDAY);
	}

	default ConcreteWeekDay Tues() {
		return weekday(Calendar.TUESDAY);
	}

	default ConcreteWeekDay Wed() {
		return weekday(Calendar.WEDNESDAY);
	}

	default ConcreteWeekDay Thur() {
		return weekday(Calendar.THURSDAY);
	}

	default ConcreteWeekDay Fri() {
		return weekday(Calendar.FRIDAY);
	}

	default ConcreteWeekDay Sat() {
		return weekday(Calendar.SATURDAY);
	}

}
