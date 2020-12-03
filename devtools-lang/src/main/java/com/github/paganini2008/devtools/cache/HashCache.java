package com.github.paganini2008.devtools.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * HashCache
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HashCache extends AbstractCache {

	private final Map<Object, Object> cache;

	public HashCache() {
		this.cache = new ConcurrentHashMap<Object, Object>();
	}

	public void putObject(Object key, Object value) {
		cache.put(key, value);
	}

	public Object getObject(Object key) {
		return cache.get(key);
	}

	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}

	public int getSize() {
		return cache.size();
	}

	public Set<Object> keys() {
		return cache.keySet();
	}

	public boolean hasKey(Object key) {
		return cache.containsKey(key);
	}

	public String toString() {
		return cache.toString();
	}

}
