package com.github.paganini2008.devtools.cache;

import java.util.Iterator;

/**
 * 
 * AbstractCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public abstract class AbstractCache implements Cache {

	public LimitedCache fifo(int maxSize, Store store) {
		LimitedCache cache = new FifoCache(this, maxSize);
		cache.setStore(store);
		return cache;
	}

	public LimitedCache lifo(int maxSize, Store store) {
		LimitedCache cache = new LifoCache(this, maxSize);
		cache.setStore(store);
		return cache;
	}

	public LimitedCache lru(int maxSize, Store store) {
		LimitedCache cache = new LruCache(this, maxSize);
		cache.setStore(store);
		return cache;
	}

	public LimitedCache sort(int maxSize, boolean asc, Store store) {
		LimitedCache cache = new SortedCache(this, maxSize, asc);
		cache.setStore(store);
		return cache;
	}

	public Cache masterStandby(Cache backup) {
		return new MasterStandbyCache(this, backup);
	}

	public ExpiredCache expiredCache(int interval) {
		return new CheckedExpiredCache(this, interval);
	}

	public ExpiredCache expiredCache() {
		return new UncheckedExpiredCache(this);
	}

	public Object getObject(Object key, Object defaultValue) {
		Object o = getObject(key);
		if (o == null) {
			o = defaultValue;
			if (o != null) {
				putObject(key, o);
				o = getObject(key);
			}
		}
		return o;
	}

	public Iterator<Object> iterator() {
		return new CacheIterator(this);
	}

	static class CacheIterator implements Iterator<Object> {

		private final Cache delegate;
		private final Iterator<Object> it;
		private Object key;

		CacheIterator(Cache delegate) {
			this.delegate = delegate;
			this.it = delegate.keys().iterator();
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public Object next() {
			key = it.next();
			return delegate.getObject(key);
		}

		public void remove() {
			delegate.removeObject(key);
		}

	}
}