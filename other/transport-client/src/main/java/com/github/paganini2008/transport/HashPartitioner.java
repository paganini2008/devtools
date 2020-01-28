package com.github.paganini2008.transport;

import java.util.Arrays;
import java.util.List;

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

	public <T> T selectChannel(Tuple tuple, List<T> channels) {
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
