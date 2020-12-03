package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

/**
 * 
 * TheDayOfWeekInMonth
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface TheDayOfWeekInMonth extends Day {

	TheDayOfWeekInMonth and(int week, int dayOfWeek);

	TheDayOfWeekInMonth andLast(int datOfWeek);

	default TheDayOfWeekInMonth andLastSat() {
		return andLast(Calendar.SATURDAY);
	}

	default TheDayOfWeekInMonth andLastFri() {
		return andLast(Calendar.FRIDAY);
	}

	default TheDayOfWeekInMonth andLastThur() {
		return andLast(Calendar.THURSDAY);
	}

	default TheDayOfWeekInMonth andLastWed() {
		return andLast(Calendar.WEDNESDAY);
	}

	default TheDayOfWeekInMonth andLastTues() {
		return andLast(Calendar.TUESDAY);
	}

	default TheDayOfWeekInMonth andLastMon() {
		return andLast(Calendar.MONDAY);
	}

	default TheDayOfWeekInMonth andLastSun() {
		return andLast(Calendar.SUNDAY);
	}
}
