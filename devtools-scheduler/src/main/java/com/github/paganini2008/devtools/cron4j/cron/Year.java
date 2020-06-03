package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Year
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Year extends Iterator<Year>, CronExpression {

	int getYear();

	int getWeekCount();

	int getLastDay();

	OneDay day(int day);

	default Day lastDay() {
		return day(getLastDay());
	}

	OneWeek week(int week);

	Week lastWeek();

	default boolean isLeapYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		return calendar.isLeapYear(getYear());
	}

	default Month everyMonth() {
		return everyMonth(1);
	}

	default Month everyMonth(int interval) {
		return everyMonth(Calendar.JANUARY, Calendar.DECEMBER, interval);
	}

	Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval);

	default Month everyMonth(int from, int to, int interval) {
		return everyMonth(y -> from, y -> to, interval);
	}

	OneMonth month(int month);

	default OneMonth Jan() {
		return month(Calendar.JANUARY);
	}

	default OneMonth Feb() {
		return month(Calendar.FEBRUARY);
	}

	default OneMonth Mar() {
		return month(Calendar.MARCH);
	}

	default OneMonth Apr() {
		return month(Calendar.APRIL);
	}

	default OneMonth May() {
		return month(Calendar.MAY);
	}

	default OneMonth June() {
		return month(Calendar.JUNE);
	}

	default OneMonth July() {
		return month(Calendar.JULY);
	}

	default OneMonth Aug() {
		return month(Calendar.AUGUST);
	}

	default OneMonth Sept() {
		return month(Calendar.SEPTEMBER);
	}

	default OneMonth Oct() {
		return month(Calendar.OCTOBER);
	}

	default OneMonth Nov() {
		return month(Calendar.NOVEMBER);
	}

	default OneMonth Dec() {
		return month(Calendar.DECEMBER);
	}
}
