package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Year
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Year extends Iterator<Year>, CronExpression {

	int getYear();

	int getWeekCount();

	int getLastDay();

	ConcreteDay day(int day);

	default Day lastDay() {
		return day(getLastDay());
	}

	ConcreteWeek week(int week);

	default ConcreteWeek lastWeek() {
		return week(getWeekCount());
	}

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

	ConcreteMonth month(int month);

	default ConcreteMonth Jan() {
		return month(Calendar.JANUARY);
	}

	default ConcreteMonth Feb() {
		return month(Calendar.FEBRUARY);
	}

	default ConcreteMonth Mar() {
		return month(Calendar.MARCH);
	}

	default ConcreteMonth Apr() {
		return month(Calendar.APRIL);
	}

	default ConcreteMonth May() {
		return month(Calendar.MAY);
	}

	default ConcreteMonth June() {
		return month(Calendar.JUNE);
	}

	default ConcreteMonth July() {
		return month(Calendar.JULY);
	}

	default ConcreteMonth Aug() {
		return month(Calendar.AUGUST);
	}

	default ConcreteMonth Sept() {
		return month(Calendar.SEPTEMBER);
	}

	default ConcreteMonth Oct() {
		return month(Calendar.OCTOBER);
	}

	default ConcreteMonth Nov() {
		return month(Calendar.NOVEMBER);
	}

	default ConcreteMonth Dec() {
		return month(Calendar.DECEMBER);
	}
}
