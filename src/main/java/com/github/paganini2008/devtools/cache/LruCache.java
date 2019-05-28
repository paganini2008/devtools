package com.github.paganini2008.devtools.cache;

import java.util.HashSet;
import java.util.Set;

import com.github.paganini2008.devtools.collection.LruList;

/**
 * LruCache
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LruCache extends LimitedCache {

	private final Cache delegate;
	private final LruList<Object> keys;
	private Store store;

	public LruCache(int maxSize) {
		this(new HashCache(), maxSize);
	}

	public LruCache(final Cache delegate, int maxSize) {
		this.delegate = delegate;
		this.keys = new LruList<Object>(maxSize) {

			private static final long serialVersionUID = -7375790466218025547L;

			public void onEviction(Object oldestKey) {
				Object oldestValue = delegate.removeObject(oldestKey);
				if (store != null) {
					store.writeObject(oldestKey, oldestValue);
				}
			}
		};
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void putObject(Object key, Object value) {
		keys.add(key);
		delegate.putObject(key, value);
	}

	public Object getObject(Object key) {
		Object result = delegate.getObject(key);
		if (result == null) {
			if (store != null) {
				result = store.readObject(key);
				if (result != null) {
					if (store.removeObject(key) != null) {
						putObject(key, result);
					}
				}
			}
		} else {
			keys.contains(key);
		}
		return result;
	}

	public Object removeObject(Object key) {
		keys.remove(key);
		Object result = delegate.removeObject(key);
		if (result == null) {
			if (store != null) {
				result = store.removeObject(key);
			}
		}
		return result;
	}

	public int getSize() {
		return delegate.getSize() + (store != null ? store.getSize() : 0);
	}

	public Set<Object> keys() {
		final Set<Object> names = new HashSet<Object>();
		names.addAll(keys);
		if (store != null) {
			names.addAll(store.keys());
		}
		return names;
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public boolean hasKey(Object key) {
		return delegate.hasKey(key);
	}

	public String toString() {
		return delegate.toString();
	}

}
