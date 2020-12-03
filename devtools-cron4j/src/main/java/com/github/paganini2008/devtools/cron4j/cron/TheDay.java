package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheDay
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface TheDay extends Day {

	TheDay andDay(int day);

	default TheDay toDay(int day) {
		return toDay(day, 1);
	}

	TheDay toDay(int day, int interval);

}
