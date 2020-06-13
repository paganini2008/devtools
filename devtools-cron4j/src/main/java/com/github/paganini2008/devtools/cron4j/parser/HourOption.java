package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.TheHour;

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
			return day.everyHour();
		}
		String[] args = value.split(",");
		TheHour hour = null;
		for (String arg : args) {
			if (hour != null) {
				hour = setHour(arg, hour);
			} else {
				hour = setHour(arg, day);
			}
		}
		return hour;
	}

	private TheHour setHour(String cron, TheHour hour) {
		if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return hour.andHour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args[0].equals("*")) {
					start = 0;
				} else {
					throw new MalformedCronException(value, e);
				}
			}
			return hour.andHour(start).toHour(23, Integer.parseInt(args[1]));
		} else {
			return hour.andHour(Integer.parseInt(cron));
		}
	}

	private TheHour setHour(String cron, Day day) {
		if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return day.hour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args[0].equals("*")) {
					start = 0;
				} else {
					throw new MalformedCronException(value, e);
				}
			}
			return day.hour(start).toHour(23, Integer.parseInt(args[1]));
		} else {
			return day.hour(Integer.parseInt(cron));
		}
	}

}
