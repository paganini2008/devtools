package com.github.paganini2008.devtools.cache;

import java.util.Set;

/**
 * CacheStore
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface CacheStore {

	void writeObject(Object key, Object eldestObject);

	Object readObject(Object key);

	Object removeObject(Object key);

	int getSize();

	Set<Object> keys();

	boolean hasKey(Object key);

}
