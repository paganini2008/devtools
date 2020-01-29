package com.github.paganini2008.transport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private final Set<String> fieldNames;

	public HashPartitioner() {
		fieldNames = new HashSet<String>();
	}

	public HashPartitioner(String[] fieldNames) {
		this();
		this.fieldNames.addAll(Arrays.asList(fieldNames));
	}

	public void addFieldNames(String... fieldNames) {
		if (fieldNames != null) {
			for (String fieldName : fieldNames) {
				this.fieldNames.add(fieldName);
			}
		}
	}

	public <T> T selectChannel(Tuple tuple, List<T> channels) {
		Object[] data = new Object[fieldNames.size()];
		int i = 0;
		for (String fieldName : fieldNames) {
			data[i++] = getFieldValue(tuple, fieldName);
		}
		try {
			return channels.get(indexFor(data, channels.size()));
		} catch (RuntimeException e) {
			return null;
		}
	}

	protected Object getFieldValue(Tuple tuple, String fieldName) {
		return tuple.getField(fieldName);
	}

	protected int indexFor(Object[] data, int length) {
		int hash = Arrays.deepHashCode(data);
		return (hash & 0x7FFFFFFF) % length;
	}

}
