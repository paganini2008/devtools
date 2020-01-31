package com.github.paganini2008.transport.serializer;

import org.nustaq.serialization.FSTConfiguration;

import com.github.paganini2008.transport.Tuple;

/**
 * 
 * FstSerializer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class FstSerializer implements Serializer {

	private final FSTConfiguration configuration = FSTConfiguration.createStructConfiguration();

	public byte[] serialize(Tuple tuple) {
		return configuration.asByteArray(tuple);
	}

	public Tuple deserialize(byte[] bytes) {
		return (Tuple) configuration.asObject(bytes);
	}

}
