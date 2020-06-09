package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * ThatDayOfWeek
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface ThatDayOfWeek extends Day {

	ThatDayOfWeek andDay(int day);

	default ThatDayOfWeek toDay(int day) {
		return toDay(day, 1);
	}

	ThatDayOfWeek toDay(int day, int interval);

	default ThatDayOfWeek toTues() {
		return toDay(Calendar.TUESDAY);
	}

	default ThatDayOfWeek toWed() {
		return toDay(Calendar.WEDNESDAY);
	}

	default ThatDayOfWeek toThur() {
		return toDay(Calendar.THURSDAY);
	}

	default ThatDayOfWeek toFri() {
		return toDay(Calendar.FRIDAY);
	}

	default ThatDayOfWeek toSat() {
		return toDay(Calendar.SATURDAY);
	}

	default ThatDayOfWeek andSun() {
		return andDay(Calendar.SUNDAY);
	}

	default ThatDayOfWeek andMon() {
		return andDay(Calendar.MONDAY);
	}

	default ThatDayOfWeek andTues() {
		return andDay(Calendar.TUESDAY);
	}

	default ThatDayOfWeek andWed() {
		return andDay(Calendar.WEDNESDAY);
	}

	default ThatDayOfWeek andThur() {
		return andDay(Calendar.THURSDAY);
	}

	default ThatDayOfWeek andFri() {
		return andDay(Calendar.FRIDAY);
	}

	default ThatDayOfWeek andSat() {
		return andDay(Calendar.SATURDAY);
	}
}
