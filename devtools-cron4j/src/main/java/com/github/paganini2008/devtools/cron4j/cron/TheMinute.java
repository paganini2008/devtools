package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheMinute
 *
 * @author Fred Feng 
 * @version 1.0
 */
public interface TheMinute extends Minute {

	TheMinute andMinute(int minute);

	default TheMinute toMinute(int minute) {
		return toMinute(minute, 1);
	}

	TheMinute toMinute(int minute, int interval);

}
