package com.github.paganini2008.devtools.scheduler;

/**
 * 
 * ConcreteDay
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteDay extends Day {

	ConcreteDay andDay(int day);
	
	default ConcreteDay andNextDay() {
		return andNextDays(1);
	}
	
	ConcreteDay andNextDays(int days);

	default ConcreteDay toDay(int day) {
		return toDay(day, 1);
	}

	ConcreteDay toDay(int day, int interval);

}
