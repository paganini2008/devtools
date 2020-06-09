package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Epoch;
import com.github.paganini2008.devtools.cron4j.cron.ThatYear;

/**
 * 
 * YearOption
 *
 * @author Fred Feng
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
		ThatYear oneYear = null;
		for (String arg : args) {
			if (oneYear != null) {
				oneYear = setYear(arg, oneYear);
			} else {
				oneYear = setYear(arg, epoch);
			}
		}
		return oneYear;
	}

	private ThatYear setYear(String cron, ThatYear oneYear) {
		if (cron.contains("-")) {
			String[] args = value.split("-", 2);
			ThatYear year = oneYear.andYear(Integer.parseInt(args[0]));
			return year.toYear(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = value.split("\\/", 2);
			ThatYear year = oneYear.andYear(Integer.parseInt(args[0]));
			return year.toYear(Epoch.LAST_YEAR_OF_THE_WORLD, Integer.parseInt(args[1]));
		} else {
			return oneYear.andYear(Integer.parseInt(cron));
		}
	}

	private ThatYear setYear(String cron, Epoch epoch) {
		if (cron.contains("-")) {
			String[] args = value.split("-", 2);
			ThatYear year = epoch.year(Integer.parseInt(args[0]));
			return year.toYear(Integer.parseInt(args[1]));
		} else if (cron.contains("/")) {
			String[] args = value.split("\\/", 2);
			ThatYear year = epoch.year(Integer.parseInt(args[0]));
			return year.toYear(Epoch.LAST_YEAR_OF_THE_WORLD, Integer.parseInt(args[1]));
		} else {
			return epoch.year(Integer.parseInt(cron));
		}
	}

}
