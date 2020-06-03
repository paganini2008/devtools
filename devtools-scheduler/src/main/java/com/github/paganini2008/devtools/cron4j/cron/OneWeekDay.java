package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * OneWeekDay
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface OneWeekDay extends Day {

	OneWeekDay andDay(int day);

	default OneWeekDay toDay(int day) {
		return toDay(day, 1);
	}

	OneWeekDay toDay(int day, int interval);

	default OneWeekDay toTues() {
		return toDay(Calendar.TUESDAY);
	}

	default OneWeekDay toWed() {
		return toDay(Calendar.WEDNESDAY);
	}

	default OneWeekDay toThur() {
		return toDay(Calendar.THURSDAY);
	}

	default OneWeekDay toFri() {
		return toDay(Calendar.FRIDAY);
	}

	default OneWeekDay toSat() {
		return toDay(Calendar.SATURDAY);
	}

	default OneWeekDay andSun() {
		return andDay(Calendar.SUNDAY);
	}

	default OneWeekDay andMon() {
		return andDay(Calendar.MONDAY);
	}

	default OneWeekDay andTues() {
		return andDay(Calendar.TUESDAY);
	}

	default OneWeekDay andWed() {
		return andDay(Calendar.WEDNESDAY);
	}

	default OneWeekDay andThur() {
		return andDay(Calendar.THURSDAY);
	}

	default OneWeekDay andFri() {
		return andDay(Calendar.FRIDAY);
	}

	default OneWeekDay andSat() {
		return andDay(Calendar.SATURDAY);
	}
}
