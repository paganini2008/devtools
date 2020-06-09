package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * ThatMinute
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface ThatMinute extends Minute {

	ThatMinute andMinute(int minute);

	default ThatMinute toMinute(int minute) {
		return toMinute(minute, 1);
	}

	ThatMinute toMinute(int minute, int interval);

}
