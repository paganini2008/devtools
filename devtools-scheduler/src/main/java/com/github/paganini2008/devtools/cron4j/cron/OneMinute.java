package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * OneMinute
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface OneMinute extends Minute {

	OneMinute andMinute(int minute);

	default OneMinute toMinute(int minute) {
		return toMinute(minute, 1);
	}

	OneMinute toMinute(int minute, int interval);

}
