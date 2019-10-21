package com.github.paganini2008.springworld.socketbird.utils;

import java.util.Arrays;
import java.util.List;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.ChannelWrapper;

/**
 * 
 * HashPartitioner
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class HashPartitioner implements Partitioner {

	private final String[] fieldNames;

	public HashPartitioner(String... fieldNames) {
		this.fieldNames = fieldNames;
	}

	public ChannelWrapper select(Tuple tuple, List<ChannelWrapper> channels) {
		Object[] data = new Object[fieldNames.length];
		int i = 0;
		for (String fieldName : fieldNames) {
			data[i++] = tuple.getField(fieldName);
		}
		try {
			return channels.get(indexFor(data, channels.size()));
		} catch (RuntimeException e) {
			return null;
		}
	}
	
	protected int indexFor(Object[] data, int length) {
		int hash = Arrays.deepHashCode(data);
		return (hash & 0x7FFFFFFF) % length;
	}

}
