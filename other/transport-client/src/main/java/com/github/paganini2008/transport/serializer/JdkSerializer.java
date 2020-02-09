package com.github.paganini2008.transport.serializer;

import java.io.InputStream;

import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * JdkSerializer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class JdkSerializer implements Serializer {

	public byte[] serialize(Tuple tuple) {
		return SerializationUtils.toByteArray(tuple);
	}

	public Tuple deserialize(byte[] bytes) {
		return SerializationUtils.readObject(bytes);
	}
	
	public Tuple deserialize(InputStream in) {
		return null;
	}

}
