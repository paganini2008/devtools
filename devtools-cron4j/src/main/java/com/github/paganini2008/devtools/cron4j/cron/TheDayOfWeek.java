package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * TheDayOfWeek
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface TheDayOfWeek extends Day {

	TheDayOfWeek andDay(int day);

	default TheDayOfWeek toDay(int day) {
		return toDay(day, 1);
	}

	TheDayOfWeek toDay(int day, int interval);

	default TheDayOfWeek toTues() {
		return toDay(Calendar.TUESDAY);
	}

	default TheDayOfWeek toWed() {
		return toDay(Calendar.WEDNESDAY);
	}

	default TheDayOfWeek toThur() {
		return toDay(Calendar.THURSDAY);
	}

	default TheDayOfWeek toFri() {
		return toDay(Calendar.FRIDAY);
	}

	default TheDayOfWeek toSat() {
		return toDay(Calendar.SATURDAY);
	}

	default TheDayOfWeek andSun() {
		return andDay(Calendar.SUNDAY);
	}

	default TheDayOfWeek andMon() {
		return andDay(Calendar.MONDAY);
	}

	default TheDayOfWeek andTues() {
		return andDay(Calendar.TUESDAY);
	}

	default TheDayOfWeek andWed() {
		return andDay(Calendar.WEDNESDAY);
	}

	default TheDayOfWeek andThur() {
		return andDay(Calendar.THURSDAY);
	}

	default TheDayOfWeek andFri() {
		return andDay(Calendar.FRIDAY);
	}

	default TheDayOfWeek andSat() {
		return andDay(Calendar.SATURDAY);
	}
}
