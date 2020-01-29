package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * OneHour
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface OneHour extends Hour {

	OneHour andHour(int hour);

	default OneHour toHour(int hour) {
		return toHour(hour, 1);
	}

	OneHour toHour(int hour, int interval);

}