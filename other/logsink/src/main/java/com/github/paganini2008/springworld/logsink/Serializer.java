package com.github.paganini2008.springworld.logsink;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * Serializer
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Serializer {
	
	byte[] serialize(Tuple tuple);

	Tuple deserialize(byte[] bytes);
}
