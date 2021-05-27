package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Hour;
import com.github.paganini2008.devtools.cron4j.cron.TheMinute;

/**
 * 
 * MinuteOption
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class MinuteOption implements CronOption {

	private final String value;

	public MinuteOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Hour hour = (Hour) cronExpression;
		try {
			return hour.minute(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return hour.everyMinute();
		}
		String[] args = value.split(",");
		TheMinute minute = null;
		for (String arg : args) {
			if (minute != null) {
				minute = setMinute(arg, minute);
			} else {
				minute = setMinute(arg, hour);
			}
		}
		return minute;
	}

	private TheMinute setMinute(String cron, TheMinute minute) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return minute.andMinute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return minute.andMinute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start = getStartValue(args[0]);
			return minute.andMinute(start).toMinute(59, Integer.parseInt(args[1]));
		} else {
			return minute.andMinute(Integer.parseInt(cron));
		}
	}

	private TheMinute setMinute(String cron, Hour hour) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return hour.minute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return hour.minute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start = getStartValue(args[0]);
			return hour.minute(start).toMinute(59, Integer.parseInt(args[1]));
		} else {
			return hour.minute(Integer.parseInt(cron));
		}
	}

	private int getStartValue(String cron) {
		try {
			return Integer.parseInt(cron);
		} catch (RuntimeException e) {
			return 0;
		}
	}

}
