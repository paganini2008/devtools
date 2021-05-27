package com.github.paganini2008.devtools.cron4j.parser;

import java.util.Date;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.EveryYear;
import com.github.paganini2008.devtools.cron4j.cron.TheYear;
import com.github.paganini2008.devtools.cron4j.cron.ThisYear;
import com.github.paganini2008.devtools.cron4j.cron.Year;
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

	private static final Epoch instance = new Epoch();

	private Epoch() {
	}

	@Override
	public Date getTime() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTimeInMillis() {
		throw new UnsupportedOperationException();
	}

	public Year everyYear(int interval) {
		return new EveryYear(DateUtils.getYear(), y -> Year.MAX_YEAR, interval);
	}

	public TheYear year(int year) {
		return new ThisYear(year);
	}

	@Override
	public CronExpression getParent() {
		return null;
	}

	public static Epoch getInstance() {
		return instance;
	}

}
