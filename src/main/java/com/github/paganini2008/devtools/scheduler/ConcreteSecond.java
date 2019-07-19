package com.github.paganini2008.devtools.scheduler;

/**
 * 
 * ConcreteSecond
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface ConcreteSecond extends Second {

	ConcreteSecond andSecond(int second);
	
	default ConcreteSecond toSecond(int second) {
		return toSecond(second, 1);
	}
	
	ConcreteSecond toSecond(int second, int interval);
	
}
