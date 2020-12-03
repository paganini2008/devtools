package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.TheYear;
import com.github.paganini2008.devtools.cron4j.cron.Year;

/**
 * 
 * YearOption
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class YearOption implements CronOption {

	private final String value;

	public YearOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Epoch epoch = (Epoch) cronExpression;
		try {
			return epoch.year(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return epoch.everyYear(1);
		}
		String[] args = value.split(",");
		TheYear year = null;
		for (String arg : args) {
			if (year != null) {
				year = setYear(arg, year);
			} else {
				year = setYear(arg, epoch);
			}
		}
		return year;
	}

	private TheYear setYear(String cron, TheYear year) {
		if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return year.andYear(Integer.parseInt(args[0])).toYear(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return year.andYear(Integer.parseInt(args[0])).toYear(Year.MAX_YEAR, Integer.parseInt(args[1]));
		} else {
			return year.andYear(Integer.parseInt(cron));
		}
	}

	private TheYear setYear(String cron, Epoch epoch) {
		if (cron.contains("-")) {
			String[] args = cron.split("-", 2);
			return epoch.year(Integer.parseInt(args[0])).toYear(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return epoch.year(Integer.parseInt(args[0])).toYear(Year.MAX_YEAR, Integer.parseInt(args[1]));
		} else {
			return epoch.year(Integer.parseInt(cron));
		}
	}

}
