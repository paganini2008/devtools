package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Epoch;
import com.github.paganini2008.devtools.cron4j.cron.OneYear;

/**
 * 
 * YearClause
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class YearClause implements Clause {

	private final String value;

	public YearClause(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Epoch epoch = (Epoch) cronExpression;
		if (value.equals("*")) {
			return epoch.everyYear(1);
		} else if (value.contains("-")) {
			String[] args = value.split("-");
			OneYear year = epoch.year(Integer.parseInt(args[0]));
			return year.toYear(Integer.parseInt(args[1]));
		} else if (value.contains(",")) {
			String[] args = value.split(",");
			OneYear oneYear = null;
			for (String arg : args) {
				if (oneYear != null) {
					oneYear = oneYear.andYear(Integer.parseInt(arg));
				} else {
					oneYear = epoch.year(Integer.parseInt(arg));
				}
			}
			return oneYear;
		} else if (value.contains("/")) {
			String[] args = value.split("\\/");
			OneYear oneYear = epoch.year(Integer.parseInt(args[0]));
			return oneYear.toYear(Epoch.LAST_YEAR_OF_THE_WORLD, Integer.parseInt(args[1]));
		}
		throw new CronParserException(value);
	}

}
