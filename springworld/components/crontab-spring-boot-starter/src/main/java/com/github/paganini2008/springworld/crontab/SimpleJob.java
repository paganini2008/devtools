package com.github.paganini2008.springworld.crontab;

import java.util.concurrent.TimeUnit;

/**
 * 
 * SimpleJob
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface SimpleJob extends Job {

	default long getDelay() {
		return getPeriod();
	}

	default TimeUnit getDelayTimeUnit() {
		return getPeriodTimeUnit();
	}

	long getPeriod();

	default TimeUnit getPeriodTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}

	default RunMode getRunMode() {
		return RunMode.FIXED_RATE;
	}

	public enum RunMode {

		FIXED_RATE, FIXED_DELAY;

	}

}
