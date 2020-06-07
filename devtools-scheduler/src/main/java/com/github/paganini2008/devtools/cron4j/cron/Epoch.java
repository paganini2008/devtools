package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Epoch
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public final class Epoch implements CronExpression {

	public static final int LAST_YEAR_OF_THE_WORLD = 9999;

	@Override
	public Date getTime() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTimeInMillis() {
		throw new UnsupportedOperationException();
	}

	public Year everyYear(int interval) {
		return new EveryYear(DateUtils.getYear(), y -> LAST_YEAR_OF_THE_WORLD, interval);
	}

	public OneYear year(int year) {
		return new SingleYear(year);
	}
	
	public CronExpression getParent() {
		return null;
	}

}
