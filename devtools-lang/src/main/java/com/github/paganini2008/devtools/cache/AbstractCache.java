package com.github.paganini2008.devtools.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * AbstractCache
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public abstract class AbstractCache implements Cache {

	public BoundedCache fifoCache(int maxSize, CacheStore store) {
		BoundedCache cache = new FifoCache(this, maxSize);
		cache.setStore(store);
		return cache;
	}

	public BoundedCache lifoCache(int maxSize, CacheStore store) {
		BoundedCache cache = new LifoCache(this, maxSize);
		cache.setStore(store);
		return cache;
	}

	public BoundedCache lruCache(int maxSize, CacheStore store) {
		BoundedCache cache = new LruCache(this, maxSize);
		cache.setStore(store);
		return cache;
	}

	public BoundedCache sortedCache(int maxSize, boolean asc, CacheStore store) {
		BoundedCache cache = new SortedCache(this, maxSize, asc);
		cache.setStore(store);
		return cache;
	}

	public Cache masterStandbyCache(Cache backup) {
		return new MasterStandbyCache(this, backup);
	}

	public ExpiredCache expiredCache(int interval) {
		return new CheckedExpiredCache(this, interval);
	}

	public ExpiredCache expiredCache() {
		return new UncheckedExpiredCache(this);
	}

	public Iterator<Object> iterator() {
		return new CacheIterator(this);
	}

	public Map<Object, Object> toEntries() {
		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
		for (Object key : keys()) {
			map.put(key, getObject(key));
		}
		return map;
	}

	/**
	 * 
	 * CacheIterator
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
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
