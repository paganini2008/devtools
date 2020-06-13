package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Minute;
import com.github.paganini2008.devtools.cron4j.cron.TheSecond;

/**
 * 
 * SecondClause
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class SecondOption implements CronOption {

	private final String value;

	public SecondOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Minute minute = (Minute) cronExpression;
		try {
			return minute.second(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}

		if (value.equals("*")) {
			return minute.everySecond();
		}
		String[] args = value.split(",");
		TheSecond second = null;
		for (String arg : args) {
			if (second != null) {
				second = setSecond(arg, second);
			} else {
				second = setSecond(arg, minute);
			}
		}
		return second;
	}

	private TheSecond setSecond(String cron, TheSecond oneSecond) {
		if (cron.contains("-")) {
			String[] args = value.split("-", 2);
			return oneSecond.andSecond(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = value.split("\\/", 2);
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
			return oneSecond.andSecond(start).toSecond(59, Integer.parseInt(args[1]));
		} else {
			return oneSecond.andSecond(Integer.parseInt(cron));
		}
	}

	private TheSecond setSecond(String cron, Minute minute) {
		if (cron.contains("-")) {
			String[] args = value.split("-", 2);
			return minute.second(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = value.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				start = 0;
			}
			return minute.second(start).toSecond(59, Integer.parseInt(args[1]));
		} else {
			return minute.second(Integer.parseInt(cron));
		}
	}

}
