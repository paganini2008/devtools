package com.github.paganini2008.devtools.scheduler;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Year
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Year extends Iterator<Year>, CurrentTime {

	int getYear();

	default Month everyMonth(int interval) {
		return everyMonth(0, 12, interval);
	}

	Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval);

	default Month everyMonth(int from, int to, int interval) {
		return everyMonth(y -> from, y -> to, interval);
	}

	ConcreteMonth month(int concrete);

}
