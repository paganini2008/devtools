package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * ConcreteMinute
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteMinute extends Minute {

	ConcreteMinute andMinute(int minute);

	default ConcreteMinute toMinute(int minute) {
		return toMinute(minute, 1);
	}

	ConcreteMinute toMinute(int minute, int interval);

}
