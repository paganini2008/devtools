package com.github.paganini2008.devtools.cache;

import java.util.Set;

/**
 * 
 * Cache
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Cache extends Iterable<Object> {

	void putObject(Object key, Object value);

	boolean hasKey(Object key);

	Object getObject(Object key);

	default Object getObject(Object key, Object defaultValue) {
		Object o = getObject(key);
		if (o == null) {
			putObject(key, defaultValue);
			o = getObject(key);
		}
		return o;
	}

	Object removeObject(Object key);

	Set<Object> keys();

	void clear();

	int getSize();

	default void close() {
	}
}
