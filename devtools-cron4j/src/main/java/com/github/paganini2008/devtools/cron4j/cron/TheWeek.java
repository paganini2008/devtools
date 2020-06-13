package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface TheWeek extends Week {

	TheWeek andWeek(int week);

	default TheWeek toWeek(int week) {
		return toWeek(week, 1);
	}

	TheWeek toWeek(int week, int interval);
}
