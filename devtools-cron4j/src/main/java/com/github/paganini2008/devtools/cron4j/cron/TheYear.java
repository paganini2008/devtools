package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface TheYear extends Year {

	TheYear andYear(int year);

	default TheYear toYear(int year) {
		return toYear(year, 1);
	}

	TheYear toYear(int year, int interval);

}
