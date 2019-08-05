package com.github.paganini2008.devtools.collection;

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
	
	String[] keys();

	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	<T> T getProperty(String key, Class<T> requiredType);

	<T> T getProperty(String key, Class<T> requiredType, T defaultValue);
	
	<T> T getRequiredProperty(String key, Class<T> requiredType);

	void fill(Object object);

	<T> T toBean(Class<T> requiredType);

}
