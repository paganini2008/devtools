package com.github.paganini2008.devtools.nio;

import com.github.paganini2008.devtools.io.SerializationException;

/**
 * 
 * NoopSerialization
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NoopSerialization implements Serialization {

	@Override
	public byte[] serialize(Object object) {
		try {
			return (byte[]) object;
		} catch (ClassCastException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		return bytes;
	}

}
