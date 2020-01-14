package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * OneYear
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface OneYear extends Year {

	OneYear andYear(int year);

	default OneYear andNextYear() {
		return andNextYears(1);
	}

	OneYear andNextYears(int years);

	default OneYear toYear(int year) {
		return toYear(year, 1);
	}

	OneYear toYear(int year, int interval);

}
