package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * ThatYear
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface ThatYear extends Year {

	ThatYear andYear(int year);

	default ThatYear toYear(int year) {
		return toYear(year, 1);
	}

	ThatYear toYear(int year, int interval);

}
