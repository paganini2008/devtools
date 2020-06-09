package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * ThatDay
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface ThatDay extends Day {

	ThatDay andDay(int day);

	default ThatDay toDay(int day) {
		return toDay(day, 1);
	}

	ThatDay toDay(int day, int interval);

}
