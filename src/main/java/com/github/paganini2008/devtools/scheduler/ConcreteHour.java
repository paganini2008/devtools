package com.github.paganini2008.devtools.scheduler;

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

}