package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * OneWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface OneWeek extends Week {

	OneWeek andWeek(int week);

	default OneWeek toWeek(int week) {
		return toWeek(week, 1);
	}

	OneWeek toWeek(int week, int interval);
}
