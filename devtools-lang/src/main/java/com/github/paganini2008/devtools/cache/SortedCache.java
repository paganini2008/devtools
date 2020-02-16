package com.github.paganini2008.devtools.cache;

import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 
 * SortedCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class SortedCache extends LimitedCache {

	private final Cache delegate;
	private final NavigableSet<Object> keys;
	private final int maxSize;
	private final boolean asc;
	private Store store;

	public SortedCache(int maxSize) {
		this(maxSize, true);
	}

	public SortedCache(int maxSize, boolean asc) {
		this(new HashCache(), maxSize, asc);
	}

	public SortedCache(Cache delegate, int maxSize, boolean asc) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.asc = asc;
		this.keys = new ConcurrentSkipListSet<Object>();
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void putObject(Object key, Object value) {
		delegate.putObject(key, value);
		control(key);
	}

	public Object getObject(Object key) {
		Object result = delegate.getObject(key);
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

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public Set<Object> keys() {
		final Set<Object> names = new LinkedHashSet<Object>();
		names.addAll(keys);
		if (store != null) {
			names.addAll(store.keys());
		}
		return names;
	}

	public boolean hasKey(Object key) {
		return delegate.hasKey(key);
	}

	private void control(Object key) {
		keys.add(key);
		if (keys.size() > maxSize) {
			Object oldestKey = asc ? keys.pollFirst() : keys.pollLast();
			Object oldestValue = delegate.removeObject(oldestKey);
			if (store != null) {
				store.writeObject(oldestKey, oldestValue);
			}
		}
	}

	public String toString() {
		return delegate.toString();
	}

	public static void main(String[] args) {
		SortedCache cache = new SortedCache(20);
		for (int i = 0; i < 30; i++) {
			cache.putObject("Key_" + i, i);
		}
		System.out.println(cache);
		System.out.println("LifoCache.main()");
	}

}
