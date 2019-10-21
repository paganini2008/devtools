package com.github.paganini2008.devtools.collection;

import java.util.Map;

import com.github.paganini2008.devtools.Case;
import com.github.paganini2008.devtools.Cases;
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

	Object[] toArray();

	Object get(String key);

	void set(String key, Object value);

	String[] keys();

	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	<T> T getProperty(String key, Class<T> requiredType);

	<T> T getProperty(String key, Class<T> requiredType, T defaultValue);

	<T> T getRequiredProperty(String key, Class<T> requiredType);

	void fill(Object object);

	<T> T toBean(Class<T> requiredType);

	Map<String, Object> toMap();

	static Tuple createBy(Map<String, ?> kwargs) {
		return createBy(Cases.LOWER, kwargs);
	}

	static Tuple createBy(Case caseFormat, Map<String, ?> kwargs) {
		TupleImpl tuple = new TupleImpl(caseFormat);
		tuple.putAll(kwargs);
		return tuple;
	}

}
