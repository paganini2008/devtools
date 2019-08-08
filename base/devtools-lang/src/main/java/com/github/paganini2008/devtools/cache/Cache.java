package com.github.paganini2008.devtools.cache;

import java.util.Set;

/**
 * 
 * Cache
 * 
 * @author Fred Feng
 * @created 2016-11
 * @revised 2019-05
 * @version 1.0
 */
public interface Cache extends Iterable<Object> {

	void putObject(Object key, Object value);

	boolean hasKey(Object key);

	Object getObject(Object key);

	Object removeObject(Object key);

	Set<Object> keys();

	void clear();

	int getSize();

	default void close() {
	}
}
