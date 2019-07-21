package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * ConcreteYear
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteYear extends Year {

	ConcreteYear andYear(int year);

	default ConcreteYear andNextYear() {
		return andNextYears(1);
	}

	ConcreteYear andNextYears(int years);

	default ConcreteYear toYear(int year) {
		return toYear(year, 1);
	}

	ConcreteYear toYear(int year, int interval);

}
