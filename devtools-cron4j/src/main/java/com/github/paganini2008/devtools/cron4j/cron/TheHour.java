package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheHour
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface TheHour extends Hour {

	TheHour andHour(int hourOfDay);

	default TheHour toHour(int hourOfDay) {
		return toHour(hourOfDay, 1);
	}

	TheHour toHour(int hourOfDay, int interval);

}
