package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.OneMonth;
import com.github.paganini2008.devtools.cron4j.cron.Year;

/**
 * 
 * MonthClause
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class MonthClause implements Clause {

	private final String value;

	public MonthClause(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Year year = (Year) cronExpression;
		try {
			return year.month(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return year.everyMonth(1);
		} else if (value.contains("-")) {
			String[] args = value.split("-", 2);
			return year.month(Integer.parseInt(args[0])).toMonth(Integer.parseInt(args[1]));
		} else if (value.contains(",")) {
			String[] args = value.split(",");
			OneMonth month = null;
			for (String arg : args) {
				if (month != null) {
					month = month.andMonth(Integer.parseInt(arg));
				} else {
					month = year.month(Integer.parseInt(arg));
				}
			}
			return month;
		} else if (value.contains("/")) {
			String[] args = value.split("\\/", 2);
			OneMonth month;
			try {
				month = year.month(Integer.parseInt(args[0]));
			} catch (NumberFormatException ignored) {
				month = year.month(1);
			}
			return month.toMonth(11, Integer.parseInt(args[1]));
		} else {
			throw new CronParserException(value);
		}
	}

}
