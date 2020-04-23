package com.github.paganini2008.devtools.nio;

/**
 * 
 * Serialization
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Serialization {

	byte[] serialize(Object object);

	Object deserialize(byte[] bytes);

}
