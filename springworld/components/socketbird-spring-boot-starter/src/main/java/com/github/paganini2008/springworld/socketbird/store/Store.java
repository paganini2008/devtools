package com.github.paganini2008.springworld.socketbird.store;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * Store
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Store {

	void set(String name, Tuple tuple);
	
	Tuple get(String name);
	
}
