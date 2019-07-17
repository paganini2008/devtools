package com.github.paganini2008.devtools.scheduler;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Day
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Day extends Iterator<Day>, CurrentTime {

	int getYear();

	int getMonth();

	int getDay();

	default Hour everyHour(int interval) {
		return everyHour(0, 23, interval);
	}

	default Hour everyHour(int from, int to, int interval) {
		return everyHour(d -> from, d -> to, interval);
	}
	
	ConcreteHour hour(int hour);

	Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval);

}
