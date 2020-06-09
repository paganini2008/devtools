package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Hour;
import com.github.paganini2008.devtools.cron4j.cron.ThatMinute;

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
		ThatMinute minute = null;
		for (String arg : args) {
			if (minute != null) {
				minute = setMinute(arg, minute);
			} else {
				minute = setMinute(arg, hour);
			}
		}
		return minute;
	}

	private ThatMinute setMinute(String cron, ThatMinute minute) {
		if (cron.equals("-")) {
			String[] args = cron.split("-", 2);
			return minute.andMinute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args[0].equals("*")) {
					start = 0;
				} else {
					throw new CronParserException(value, e);
				}
			}
			return minute.andMinute(start).toMinute(59, Integer.parseInt(args[1]));
		} else {
			return minute.andMinute(Integer.parseInt(cron));
		}
	}

	private ThatMinute setMinute(String cron, Hour hour) {
		if (cron.equals("-")) {
			String[] args = cron.split("-", 2);
			return hour.minute(Integer.parseInt(args[0])).toMinute(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args[0].equals("*")) {
					start = 0;
				} else {
					throw new CronParserException(value, e);
				}
			}
			return hour.minute(start).toMinute(59, Integer.parseInt(args[1]));
		} else {
			return hour.minute(Integer.parseInt(cron));
		}
	}

}
