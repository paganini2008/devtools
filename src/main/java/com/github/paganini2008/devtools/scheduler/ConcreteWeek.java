package com.github.paganini2008.devtools.scheduler;

/**
 * 
 * ConcreteWeek
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteWeek extends Week {

	ConcreteWeek andWeek(int week);
	
	default ConcreteWeek toWeek(int week) {
		return toWeek(week, 1);
	}

	ConcreteWeek toWeek(int week, int interval);
}
