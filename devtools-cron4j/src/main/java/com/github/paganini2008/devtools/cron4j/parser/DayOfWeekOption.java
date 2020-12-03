package com.github.paganini2008.devtools.cron4j.parser;

import java.util.Calendar;

import com.github.paganini2008.devtools.cron4j.cron.CalendarUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.TheDayOfWeek;
import com.github.paganini2008.devtools.cron4j.cron.ThisDayOfWeekInMonth;

/**
 * 
 * DayOfWeekOption
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class DayOfWeekOption implements CronOption {

	private final String value;

	public DayOfWeekOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Month month = (Month) cronExpression;
		try {
			return month.everyWeek().day(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			try {
				return month.everyWeek().day(CalendarUtils.getDayOfWeekValue(value));
			} catch (MalformedCronException ignored) {
			}
		}
		if (value.equals("*")) {
			return month.everyWeek().everyDay();
		}
		String[] args = value.split(",");
		Day dayOfWeek = null;
		for (String arg : args) {
			try {
				if (dayOfWeek != null) {
					dayOfWeek = setDayOfWeek(arg, dayOfWeek);
				} else {
					dayOfWeek = setDayOfWeek(arg, month);
				}
			} catch (ClassCastException e) {
				throw new MalformedCronException(value, e);
			}
		}
		return dayOfWeek;
	}

	private Day setDayOfWeek(String cron, Day day) {
		if (cron.endsWith("L")) {
			return ((ThisDayOfWeekInMonth) day).andLast(Integer.parseInt(cron.substring(0, 1)));
		} else if (cron.contains("#")) {
			String[] args = cron.split("#", 2);
			return ((ThisDayOfWeekInMonth) day).and(Integer.parseInt(args[1]), Integer.parseInt(args[0]));
		} else if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return ((TheDayOfWeek) day).andDay(CalendarUtils.getDayOfWeekValue(args[0])).toDay(CalendarUtils.getDayOfWeekValue(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return ((TheDayOfWeek) day).andDay(Integer.parseInt(args[0])).toDay(Calendar.SATURDAY, Integer.parseInt(args[1]));
		} else {
			return ((TheDayOfWeek) day).andDay(Integer.parseInt(cron));
		}
	}

	private Day setDayOfWeek(String cron, Month month) {
		if (cron.endsWith("L")) {
			return month.lastDayOfWeek(Integer.parseInt(cron.substring(0, 1)));
		} else if (cron.contains("#")) {
			String[] args = cron.split("#", 2);
			return month.dayOfWeek(Integer.parseInt(args[1]), Integer.parseInt(args[0]));
		} else if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return month.everyWeek().day(CalendarUtils.getDayOfWeekValue(args[0])).toDay(CalendarUtils.getDayOfWeekValue(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return month.everyWeek().day(Integer.parseInt(args[0])).toDay(Calendar.SATURDAY, Integer.parseInt(args[1]));
		} else {
			return month.everyWeek().day(Integer.parseInt(cron));
		}
	}

}
