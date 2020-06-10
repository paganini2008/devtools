package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.ThatDay;

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
		}
		String[] args = value.split(",");
		ThatDay day = null;
		for (String arg : args) {
			if (day != null) {
				day = setDay(arg, day, month);
			} else {
				day = setDay(arg, month);
			}
		}
		return day;
	}

	private ThatDay setDay(String cron, ThatDay day, Month month) {
		if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return day.andDay(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args[0].equals("*")) {
					start = 1;
				} else {
					throw new MalformedCronException(value, e);
				}
			}
			return day.andDay(start).toDay(month.getLasyDay(), Integer.parseInt(args[1]));
		} else {
			return day.andDay(Integer.parseInt(cron));
		}
	}

	private ThatDay setDay(String cron, Month month) {
		if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return month.day(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args[0].equals("*")) {
					start = 1;
				} else {
					throw new MalformedCronException(value, e);
				}
			}
			return month.day(start).toDay(month.getLasyDay(), Integer.parseInt(args[1]));
		} else {
			return month.day(Integer.parseInt(cron));
		}
	}

}
