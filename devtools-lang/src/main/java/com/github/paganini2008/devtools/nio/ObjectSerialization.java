package com.github.paganini2008.devtools.nio;

import com.github.paganini2008.devtools.io.SerializationUtils;

/**
 * 
 * ObjectSerialization
 *
 * @author Fred Feng
 * @since 1.0
 */
public class ObjectSerialization implements Serialization {

	public byte[] serialize(Object serializable) {
		return SerializationUtils.serialize(serializable, false);
	}

	public Object deserialize(byte[] bytes) {
		return SerializationUtils.deserialize(bytes, false);
	}

}
