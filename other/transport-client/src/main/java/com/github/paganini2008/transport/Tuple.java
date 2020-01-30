package com.github.paganini2008.transport;

import java.util.Map;

/**
 * 
 * Tuple
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Tuple extends Cloneable {

	boolean hasField(String fieldName);

	void setField(String fieldName, Object value);

	Object getField(String fieldName);

	<T> T getField(String fieldName, Class<T> requiredType);

	void fill(Object object);

	Map<String, Object> toMap();

	Tuple clone();

	public static Tuple newTuple() {
		return new TupleImpl();
	}

	public static Tuple by(String content) {
		Tuple tuple = new TupleImpl();
		tuple.setField("content", content);
		return tuple;
	}

	public static Tuple wrap(Map<String, ?> kwargs) {
		return new TupleImpl(kwargs);
	}

}
