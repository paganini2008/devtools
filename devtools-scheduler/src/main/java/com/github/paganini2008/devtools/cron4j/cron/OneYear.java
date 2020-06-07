package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * OneYear
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface OneYear extends Year {

	OneYear andYear(int year);

	default OneYear toYear(int year) {
		return toYear(year, 1);
	}

	OneYear toYear(int year, int interval);

}
