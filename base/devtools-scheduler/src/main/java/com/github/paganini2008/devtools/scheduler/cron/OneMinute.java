package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * OneMinute
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface OneMinute extends Minute {

	OneMinute andMinute(int minute);

	default OneMinute toMinute(int minute) {
		return toMinute(minute, 1);
	}

	OneMinute toMinute(int minute, int interval);

}
