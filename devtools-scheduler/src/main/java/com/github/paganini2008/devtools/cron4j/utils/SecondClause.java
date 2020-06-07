package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Minute;
import com.github.paganini2008.devtools.cron4j.cron.OneSecond;

/**
 * 
 * SecondClause
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class SecondClause implements Clause {

	private final String value;

	public SecondClause(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Minute minute = ((Minute) cronExpression);
		try {
			int second = Integer.parseInt(value);
			return minute.second(second);
		} catch (NumberFormatException ignored) {
		}

		if (value.equals("*")) {
			return minute.everySecond();
		} else if (value.contains("-")) {
			String[] args = value.split("-", 2);
			return minute.second(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1]));
		} else if (value.contains(",")) {
			String[] args = value.split(",");
			OneSecond second = null;
			for (String arg : args) {
				if (second != null) {
					second = second.andSecond(Integer.parseInt(arg));
				} else {
					second = minute.second(Integer.parseInt(arg));
				}
			}
			return second;
		} else if (value.contains("/")) {
			String[] args = value.split("\\/", 2);
			int start;
			try {
				start = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				start = 0;
			}
			return minute.second(start).toSecond(59, Integer.parseInt(args[1]));
		} else {
			throw new CronParserException(value);
		}
	}

}
