package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * ThatWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ThatWeek extends Week {

	ThatWeek andWeek(int week);

	default ThatWeek toWeek(int week) {
		return toWeek(week, 1);
	}

	ThatWeek toWeek(int week, int interval);
}
