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

	default Day everyDay(int interval) {
		return everyDay(Calendar.SUNDAY, Calendar.SATURDAY, interval);
	}

	default Day everyDay(int from, int to, int interval) {
		return everyDay(w -> from, w -> to, interval);
	}

	Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval);

	ConcreteDay weekday(int day);

	default ConcreteDay workdays() {
		ConcreteDay days = Mon();
		for (int i = Calendar.TUESDAY; i <= Calendar.FRIDAY; i++) {
			days = days.and(i);
		}
		return days;
	}

	default ConcreteDay Sun() {
		return weekday(Calendar.SUNDAY);
	}

	default ConcreteDay Mon() {
		return weekday(Calendar.MONDAY);
	}

	default ConcreteDay Tues() {
		return weekday(Calendar.TUESDAY);
	}

	default ConcreteDay Wed() {
		return weekday(Calendar.WEDNESDAY);
	}

	default ConcreteDay Thur() {
		return weekday(Calendar.THURSDAY);
	}

	default ConcreteDay Fri() {
		return weekday(Calendar.FRIDAY);
	}

	default ConcreteDay Sat() {
		return weekday(Calendar.SATURDAY);
	}

}
