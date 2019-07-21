package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * ConcreteHour
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteHour extends Hour {

	ConcreteHour andHour(int hour);

	default ConcreteHour toHour(int hour) {
		return toHour(hour, 1);
	}

	ConcreteHour toHour(int hour, int interval);

}
