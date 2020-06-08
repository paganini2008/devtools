package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.OneHour;

/**
 * 
 * HourOption
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class HourOption implements CronOption {

	private final String value;

	public HourOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Day day = (Day) cronExpression;
		try {
			return day.hour(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return day.everyHour(1);
		} else if (value.contains("-")) {
			String[] args = value.split("-", 2);
			return day.hour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]));
		} else if (value.contains(",")) {
			String[] args = value.split(",");
			OneHour hour = null;
			for (String arg : args) {
				if (hour != null) {
					hour = hour.andHour(Integer.parseInt(arg));
				} else {
					hour = day.hour(Integer.parseInt(arg));
				}
			}
			return hour;
		} else if (value.contains("/")) {
			String[] args = value.split("\\/", 2);
			OneHour hour;
			try {
				hour = day.hour(Integer.parseInt(args[0]));
			} catch (NumberFormatException ignored) {
				hour = day.hour(0);
			}
			return hour.toHour(23, Integer.parseInt(args[1]));
		} else {
			throw new CronParserException(value);
		}
	}

}
