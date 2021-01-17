package com.github.paganini2008.devtools.cache;

import java.util.Set;

/**
 * CacheStore
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public interface CacheStore {

	void writeObject(Object name, Object o);

	Object readObject(Object name);

	Object removeObject(Object name);

	int getSize();

	Set<Object> keys();

}
