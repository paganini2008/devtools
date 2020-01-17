package com.github.paganini2008.springworld.webcrawler.core;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * BatchCondition
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface BatchCondition {

	boolean finish(Tuple tuple);

	default void afterFinish(Tuple tuple) {
	}

}
