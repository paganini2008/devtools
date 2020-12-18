package com.github.paganini2008.devtools.cache;

import java.util.Set;
import java.util.function.Supplier;

/**
 * 
 * Cache
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Cache extends Iterable<Object> {

	default void putObject(Object key, Object value) {
		putObject(key, value, false);
	}

	void putObject(Object key, Object value, boolean ifAbsent);

	boolean hasKey(Object key);

	Object getObject(Object key);

	default Object getObject(Object key, Object defaultValue) {
		return getObject(key, () -> defaultValue);
	}

	default Object getObject(Object key, Supplier<Object> defaultValue) {
		Object o = getObject(key);
		if (o == null) {
			putObject(key, defaultValue.get());
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
