package com.github.paganini2008.devtools.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * LruCache
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public class LruCache extends BoundedCache {

	private final LruMap<Object, Object> lruMap;
	private CacheStore store;

	public LruCache(int maxSize) {
		this.lruMap = new LruMap<Object, Object>(new ConcurrentHashMap<Object, Object>(), maxSize) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Object eldestKey, Object eldestValue) {
				if (store != null) {
					store.writeObject(eldestKey, eldestValue);
				}
			}

		};
	}

	protected LruCache(LruMap<Object, Object> lruMap) {
		this.lruMap = lruMap;
	}

	public void setStore(CacheStore store) {
		this.store = store;
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent) {
			lruMap.putIfAbsent(key, value);
		} else {
			lruMap.put(key, value);
		}
	}

	public Object getObject(Object key) {
		Object result = lruMap.get(key);
		if (result == null) {
			if (store != null) {
				result = store.readObject(key);
				if (result != null) {
					if (store.removeObject(key) != null) {
						putObject(key, result);
					}
				}
			}
		}
		return result;
	}

	public Object removeObject(Object key) {
		Object result = lruMap.remove(key);
		if (result == null) {
			if (store != null) {
				result = store.removeObject(key);
			}
		}
		return result;
	}

	public int getSize() {
		return lruMap.size() + (store != null ? store.getSize() : 0);
	}

	public Set<Object> keys() {
		final Set<Object> keys = new HashSet<Object>();
		keys.addAll(lruMap.keySet());
		if (store != null) {
			keys.addAll(store.keys());
		}
		return Collections.unmodifiableSet(keys);
	}

	public void clear() {
		lruMap.clear();
	}

	public boolean hasKey(Object key) {
		return (lruMap.containsKey(key)) || (store != null && store.hasKey(key));
	}

	public String toString() {
		return lruMap.toString();
	}

}
