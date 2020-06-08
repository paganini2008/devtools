package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Hour;
import com.github.paganini2008.devtools.cron4j.cron.OneMinute;

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
			return hour.everyMinute(1);
		} else if (value.equals("-")) {
			String[] args = value.split("-", 2);
			return hour.minute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]));
		} else if (value.equals(",")) {
			String[] args = value.split(",");
			OneMinute minute = null;
			for (String arg : args) {
				if (minute != null) {
					minute = minute.andMinute(Integer.parseInt(arg));
				} else {
					minute = hour.minute(Integer.parseInt(arg));
				}
			}
			return minute;
		} else if (value.contains("/")) {
			String[] args = value.split("\\/", 2);
			OneMinute minute;
			try {
				minute = hour.minute(Integer.parseInt(args[0]));
			} catch (NumberFormatException e) {
				minute = hour.minute(0);
			}
			return minute.toMinute(59, Integer.parseInt(args[1]));
		} else {
			throw new CronParserException(value);
		}

	}

}
