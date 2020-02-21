package com.github.paganini2008.devtools.collection;

import java.util.Map;

import com.github.paganini2008.devtools.CaseFormat;

/**
 * 
 * Tuple
 *
 * @author Fred Feng
 * 
 * 
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

	Object[] toValues();

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
		return newTuple(CaseFormat.DEFAULT);
	}

	static Tuple newTuple(CaseFormat caseFormat) {
		return new TupleImpl(caseFormat);
	}

	static Tuple wrap(Map<String, Object> kwargs) {
		return wrap(kwargs, CaseFormat.DEFAULT);
	}

	static Tuple wrap(Map<String, Object> kwargs, CaseFormat caseFormat) {
		Tuple tuple = newTuple(caseFormat);
		tuple.append(kwargs);
		return tuple;
	}

}
