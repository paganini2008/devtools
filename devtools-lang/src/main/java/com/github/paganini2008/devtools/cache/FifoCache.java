package com.github.paganini2008.devtools.cache;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * FifoCache
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FifoCache extends BoundedCache {

	private final Cache delegate;
	private final Queue<Object> keys;
	private final int maxSize;
	private CacheStore store;

	public FifoCache(int maxSize) {
		this(new HashCache(), maxSize);
	}

	public FifoCache(Cache delegate, int maxSize) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = new ConcurrentLinkedQueue<Object>();
	}

	public void setStore(CacheStore store) {
		this.store = store;
	}

	public int getSize() {
		return delegate.getSize() + (store != null ? store.getSize() : 0);
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		delegate.putObject(key, value, ifAbsent);
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

	public void clear() {
		delegate.clear();
		keys.clear();
	}

	private void control(Object key) {
		keys.add(key);
		if (keys.size() > maxSize) {
			Object oldestKey = keys.poll();
			Object oldestValue = delegate.removeObject(oldestKey);
			if (store != null) {
				store.writeObject(oldestKey, oldestValue);
			}
		}
	}

	public Set<Object> keys() {
		final Set<Object> names = new HashSet<Object>();
		names.addAll(keys);
		if (store != null) {
			names.addAll(store.keys());
		}
		return names;
	}

	public boolean hasKey(Object key) {
		return delegate.hasKey(key);
	}

	public String toString() {
		return delegate.toString();
	}

}
