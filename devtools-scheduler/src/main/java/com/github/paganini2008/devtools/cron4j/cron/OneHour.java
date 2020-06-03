package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * OneHour
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface OneHour extends Hour {

	OneHour andHour(int hourOfDay);

	default OneHour toHour(int hourOfDay) {
		return toHour(hourOfDay, 1);
	}

	OneHour toHour(int hourOfDay, int interval);

}
