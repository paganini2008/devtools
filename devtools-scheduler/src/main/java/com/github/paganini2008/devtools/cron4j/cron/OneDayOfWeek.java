package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * OneDayOfWeek
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface OneDayOfWeek extends Day {

	OneDayOfWeek andDay(int day);

	default OneDayOfWeek toDay(int day) {
		return toDay(day, 1);
	}

	OneDayOfWeek toDay(int day, int interval);

	default OneDayOfWeek toTues() {
		return toDay(Calendar.TUESDAY);
	}

	default OneDayOfWeek toWed() {
		return toDay(Calendar.WEDNESDAY);
	}

	default OneDayOfWeek toThur() {
		return toDay(Calendar.THURSDAY);
	}

	default OneDayOfWeek toFri() {
		return toDay(Calendar.FRIDAY);
	}

	default OneDayOfWeek toSat() {
		return toDay(Calendar.SATURDAY);
	}

	default OneDayOfWeek andSun() {
		return andDay(Calendar.SUNDAY);
	}

	default OneDayOfWeek andMon() {
		return andDay(Calendar.MONDAY);
	}

	default OneDayOfWeek andTues() {
		return andDay(Calendar.TUESDAY);
	}

	default OneDayOfWeek andWed() {
		return andDay(Calendar.WEDNESDAY);
	}

	default OneDayOfWeek andThur() {
		return andDay(Calendar.THURSDAY);
	}

	default OneDayOfWeek andFri() {
		return andDay(Calendar.FRIDAY);
	}

	default OneDayOfWeek andSat() {
		return andDay(Calendar.SATURDAY);
	}
}
