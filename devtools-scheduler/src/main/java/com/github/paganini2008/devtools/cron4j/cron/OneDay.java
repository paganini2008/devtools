package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * OneDay
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface OneDay extends Day {

	OneDay andDay(int day);

	default OneDay toDay(int day) {
		return toDay(day, 1);
	}

	OneDay toDay(int day, int interval);

}
