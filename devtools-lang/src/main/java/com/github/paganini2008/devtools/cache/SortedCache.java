package com.github.paganini2008.devtools.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.SortedBoundedMap;

/**
 * 
 * SortedCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class SortedCache extends BoundedCache {

	private final SortedBoundedMap<Object, Object> boundedMap;

	public SortedCache(int maxSize) {
		this.boundedMap = new SortedBoundedMap<Object, Object>(new ConcurrentHashMap<Object, Object>(), maxSize) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Object eldestKey, Object eldestValue) {
				if (store != null) {
					store.writeObject(eldestKey, eldestValue);
				} else {
					dispose(eldestKey, eldestValue);
				}
			}

		};
	}

	private CacheStore store;

	@Override
	public void setCacheStore(CacheStore store) {
		this.store = store;
	}

	public void setAsc(boolean asc) {
		this.boundedMap.setAsc(asc);
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent) {
			boundedMap.putIfAbsent(key, value);
		} else {
			boundedMap.put(key, value);
		}
	}

	public Object getObject(Object key) {
		Object result = boundedMap.get(key);
		if (result == null) {
			if (store != null) {
				result = store.readObject(key);
				if (result != null) {
					store.removeObject(key);
					putObject(key, result);
				}
			}
		}
		return result;
	}

	public Object removeObject(Object key) {
		Object result = boundedMap.remove(key);
		if (result == null) {
			if (store != null) {
				result = store.removeObject(key);
			}
		}
		if (result != null) {
			dispose(key, result);
		}
		return result;
	}

	public int getSize() {
		return boundedMap.size() + (store != null ? store.getSize() : 0);
	}

	public void clear() {
		boundedMap.clear();
	}

	public Set<Object> keys() {
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(boundedMap.keySet());
		if (store != null) {
			keys.addAll(store.keys());
		}
		return Collections.unmodifiableSet(keys);
	}

	public boolean hasKey(Object key) {
		return (boundedMap.containsKey(key)) || (store != null && store.hasKey(key));
	}

	public String toString() {
		return boundedMap.toString();
	}

	public static void main(String[] args) {
		SortedCache cache = new SortedCache(10);
		for (int i = 0; i < 50; i++) {
			cache.putObject("Key_" + i, i);
		}
		System.out.println(cache);
		System.out.println("LifoCache.main()");
	}

}
