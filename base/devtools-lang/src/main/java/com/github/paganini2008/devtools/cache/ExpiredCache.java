package com.github.paganini2008.devtools.cache;

/**
 * ExpiredCache
 * 
 * @author Fred Feng
 * @created 2016-11
 * @version 1.0
 */
public interface ExpiredCache extends Cache {

	void putObject(Object key, Object value, int expired);

	void setExpired(Object key, int expired);

	interface CachedObject {

		int getExpired();

		void setExpired(int expired);

		Object getValue();

		long getCreated();
	}
}
