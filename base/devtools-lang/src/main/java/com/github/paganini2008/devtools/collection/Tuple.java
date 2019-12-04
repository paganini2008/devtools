package com.github.paganini2008.devtools.collection;

import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.CaseFormat;
import com.github.paganini2008.devtools.beans.TupleImpl;

/**
 * 
 * Tuple
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public interface Tuple {

	default boolean isEmpty() {
		return size() == 0;
	}

	int size();

	Object get(String key);

	void set(String key, Object value);

	String[] keys();

	Object[] valueArray();

	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	<T> T getProperty(String key, Class<T> requiredType);

	<T> T getProperty(String key, Class<T> requiredType, T defaultValue);

	<T> T getRequiredProperty(String key, Class<T> requiredType);

	void fill(Object object);

	<T> T toBean(Class<T> requiredType);

	Map<String, Object> toMap();

	void append(Map<String, Object> m);

	static Tuple newTuple() {
		return wrap(new HashMap<String, Object>());
	}

	static Tuple wrap(Map<String, Object> kwargs) {
		return new TupleImpl(kwargs, CaseFormat.DEFAULT);
	}

}
