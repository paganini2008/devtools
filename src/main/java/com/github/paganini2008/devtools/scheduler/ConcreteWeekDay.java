package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;

/**
 * 
 * ConcreteWeekDay
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteWeekDay extends Day {

	ConcreteWeekDay andDay(int day);

	default ConcreteWeekDay toDay(int day) {
		return toDay(day, 1);
	}

	ConcreteWeekDay toDay(int day, int interval);

	default ConcreteWeekDay toTues() {
		return toDay(Calendar.TUESDAY);
	}

	default ConcreteWeekDay toWed() {
		return toDay(Calendar.WEDNESDAY);
	}

	default ConcreteWeekDay toThur() {
		return toDay(Calendar.THURSDAY);
	}

	default ConcreteWeekDay toFri() {
		return toDay(Calendar.FRIDAY);
	}

	default ConcreteWeekDay toSat() {
		return toDay(Calendar.SATURDAY);
	}

	default ConcreteWeekDay andSun() {
		return andDay(Calendar.SUNDAY);
	}

	default ConcreteWeekDay andMon() {
		return andDay(Calendar.MONDAY);
	}

	default ConcreteWeekDay andTues() {
		return andDay(Calendar.TUESDAY);
	}

	default ConcreteWeekDay andWed() {
		return andDay(Calendar.WEDNESDAY);
	}

	default ConcreteWeekDay andThur() {
		return andDay(Calendar.THURSDAY);
	}

	default ConcreteWeekDay andFri() {
		return andDay(Calendar.FRIDAY);
	}

	default ConcreteWeekDay andSat() {
		return andDay(Calendar.SATURDAY);
	}
}
