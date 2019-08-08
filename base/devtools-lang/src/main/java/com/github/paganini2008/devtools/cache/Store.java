package com.github.paganini2008.devtools.cache;

import java.util.Set;

/**
 * Store
 * 
 * @author Fred Feng
 * @created 2016-11
 * @version 1.0
 */
public interface Store {

	void writeObject(Object name, Object o);

	Object readObject(Object name);

	Object removeObject(Object name);

	int getSize();

	Set<Object> keys();

}
