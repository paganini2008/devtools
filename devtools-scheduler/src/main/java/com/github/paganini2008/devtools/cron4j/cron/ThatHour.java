package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * ThatHour
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ThatHour extends Hour {

	ThatHour andHour(int hourOfDay);

	default ThatHour toHour(int hourOfDay) {
		return toHour(hourOfDay, 1);
	}

	ThatHour toHour(int hourOfDay, int interval);

}
