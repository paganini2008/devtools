package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;

/**
 * 
 * CalendarAssert
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public abstract class CalendarAssert {

	public static void checkMonth(int month) {
		if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
			throw new IllegalArgumentException("Month's range is " + Calendar.JANUARY + " to " + Calendar.DECEMBER);
		}
	}

	public static void checkWeekOfMonth(Month month, int week) {
		if (week < 1 || week > month.getWeekCount()) {
			throw new IllegalArgumentException("Week's range is 1 to " + month.getWeekCount());
		}
	}

	public static void checkDayOfMonth(Month month, int day) {
		if (day < 1 || day > month.getLasyDay()) {
			throw new IllegalArgumentException("Day's range is 1 to " + month.getLasyDay());
		}
	}

	public static void checkWeekDay(int weekday) {
		if (weekday < Calendar.SUNDAY || weekday > Calendar.SATURDAY) {
			throw new IllegalArgumentException("WeekDay's range is " + Calendar.SUNDAY + " to " + Calendar.SATURDAY);
		}
	}

}
