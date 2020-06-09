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

	ThatDay day(int day);

	default Day lastDay() {
		return day(getLastDay());
	}

	ThatWeek week(int week);

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

	ThatMonth month(int month);

	default ThatMonth Jan() {
		return month(Calendar.JANUARY);
	}

	default ThatMonth Feb() {
		return month(Calendar.FEBRUARY);
	}

	default ThatMonth Mar() {
		return month(Calendar.MARCH);
	}

	default ThatMonth Apr() {
		return month(Calendar.APRIL);
	}

	default ThatMonth May() {
		return month(Calendar.MAY);
	}

	default ThatMonth June() {
		return month(Calendar.JUNE);
	}

	default ThatMonth July() {
		return month(Calendar.JULY);
	}

	default ThatMonth Aug() {
		return month(Calendar.AUGUST);
	}

	default ThatMonth Sept() {
		return month(Calendar.SEPTEMBER);
	}

	default ThatMonth Oct() {
		return month(Calendar.OCTOBER);
	}

	default ThatMonth Nov() {
		return month(Calendar.NOVEMBER);
	}

	default ThatMonth Dec() {
		return month(Calendar.DECEMBER);
	}
}
