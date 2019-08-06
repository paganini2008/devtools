package com.github.paganini2008.springworld.support;

/**
 * 
 * IdGenerator
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public interface IdGenerator {

	long currentValue();

	long nextValue();

	default void destroy() {
	}

}
