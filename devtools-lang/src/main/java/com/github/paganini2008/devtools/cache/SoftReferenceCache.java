package com.github.paganini2008.devtools.cache;

import java.util.HashSet;
import java.util.Set;

import com.github.paganini2008.devtools.collection.SoftReferenceMap;

/**
 * 
 * SoftReferenceCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class SoftReferenceCache extends AbstractCache {

	private final SoftReferenceMap<Object, Object> cache;

	public SoftReferenceCache() {
		cache = new SoftReferenceMap<Object, Object>();
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent) {
			cache.putIfAbsent(key, value);
		} else {
			cache.put(key, value);
		}
	}

	public boolean hasKey(Object key) {
		return cache.containsKey(key);
	}

	public Object getObject(Object key) {
		return cache.get(key);
	}

	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	public Set<Object> keys() {
		return new HashSet<Object>(cache.keySet());
	}

	public void clear() {
		cache.clear();
	}

	public int getSize() {
		return cache.size();
	}

	public String toString() {
		return cache.toString();
	}

}
