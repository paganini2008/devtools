package com.github.paganini2008.devtools.cron4j.utils;

import java.util.Calendar;

import com.github.paganini2008.devtools.cron4j.cron.CalendarUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.ThatDayOfWeek;

/**
 * 
 * DayOfWeekOption
 *
 * @author Fred Feng
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
			} catch (CronParserException ignored) {
			}
		}
		if (value.equals("*")) {
			return month.everyWeek().everyDay();
		}
		String[] args = value.split(",");
		ThatDayOfWeek dayOfWeek = null;
		for (String arg : args) {
			if (dayOfWeek != null) {
				dayOfWeek = setDayOfWeek(arg, dayOfWeek, month);
			} else {
				dayOfWeek = setDayOfWeek(arg, month);
			}
		}
		return dayOfWeek;
	}

	private ThatDayOfWeek setDayOfWeek(String cron, ThatDayOfWeek dayOfWeek, Month month) {
		if (value.endsWith("L")) {
			return month.lastWeek().day(Integer.parseInt(value.substring(0, 1)));
		} else if (value.contains("#")) {
			String[] args = value.split("#", 2);
			return month.week(Integer.parseInt(args[1])).day(Integer.parseInt(args[0]));
		} else if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return dayOfWeek.andDay(CalendarUtils.getDayOfWeekValue(args[0])).toDay(CalendarUtils.getDayOfWeekValue(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return dayOfWeek.andDay(Integer.parseInt(args[0])).toDay(Calendar.SATURDAY, Integer.parseInt(args[1]));
		} else {
			return dayOfWeek.andDay(Integer.parseInt(cron));
		}
	}

	private ThatDayOfWeek setDayOfWeek(String cron, Month month) {
		if (value.endsWith("L")) {
			return month.lastWeek().day(Integer.parseInt(value.substring(0, 1)));
		} else if (value.contains("#")) {
			String[] args = value.split("#", 2);
			return month.week(Integer.parseInt(args[1])).day(Integer.parseInt(args[0]));
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
