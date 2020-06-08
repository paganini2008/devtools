package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.OneDay;

/**
 * 
 * DayOption
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class DayOption implements CronOption {

	private final String value;

	public DayOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Month month = (Month) cronExpression;
		try {
			return month.day(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return month.everyDay();
		} else if (value.equals("L")) {
			return month.lastDay();
		} else if (value.equals("LW")) {
			return month.lastWeek().Fri();
		} else if (value.contains("-")) {
			String[] args = value.split("-", 2);
			return month.day(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]));
		} else if (value.contains(",")) {
			String[] args = value.split(",");
			OneDay day = null;
			for (String arg : args) {
				if (day != null) {
					day = day.andDay(Integer.parseInt(arg));
				} else {
					day = month.day(Integer.parseInt(arg));
				}
			}
			return day;
		} else if (value.contains("/")) {
			String[] args = value.split("\\/", 2);
			OneDay day;
			try {
				day = month.day(Integer.parseInt(args[0]));
			} catch (NumberFormatException ignored) {
				day = month.day(1);
			}
			return day.toDay(month.getLasyDay(), Integer.parseInt(args[1]));
		} else {
			throw new CronParserException(value);
		}
	}

}
