package com.github.paganini2008.springworld.socketbird;

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
public interface Tuple {

	void setField(String fieldName, Object value);

	Object getField(String fieldName);

	<T> T getField(String fieldName, Class<T> requiredType);

	void fill(Object object);

	Map<String, Object> toMap();

	public static Tuple createBy(Map<String, ?> kwargs) {
		return new TupleImpl(kwargs);
	}

}
