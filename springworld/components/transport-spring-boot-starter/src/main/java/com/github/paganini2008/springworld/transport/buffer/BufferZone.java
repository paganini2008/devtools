package com.github.paganini2008.springworld.transport.buffer;

import com.github.paganini2008.transport.Tuple;

/**
 * 
 * BufferZone
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface BufferZone {

	default void configure() throws Exception {
	}

	default void destroy() {
	}

	void set(String name, Tuple tuple) throws Exception;

	Tuple get(String name) throws Exception;

	int size(String name) throws Exception;

}
