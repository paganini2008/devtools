package com.github.paganini2008.devtools.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * UncheckedExpiredCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class UncheckedExpiredCache extends AbstractCache implements ExpiredCache {

	static class DefaultCachedObject implements CachedObject {

		final Object value;
		final long created;
		int expired;

		DefaultCachedObject(Object value, long created, int expired) {
			this.value = value;
			this.created = created;
			this.expired = expired;
		}

		public int getExpired() {
			return expired;
		}

		public void setExpired(int expired) {
			this.expired = expired;
		}

		public Object getValue() {
			return value;
		}

		public long getCreated() {
			return created;
		}

	}

	private final Cache delegate;

	public UncheckedExpiredCache(Cache delegate) {
		this.delegate = delegate;
	}

	public void putObject(Object key, Object value) {
		putObject(key, value, 3 * 60);
	}

	public void putObject(Object key, Object value, int expired) {
		DefaultCachedObject cachedObject = new DefaultCachedObject(value, System.currentTimeMillis(), expired);
		delegate.putObject(key, cachedObject);
	}

	public void setExpired(Object key, int expired) {
		DefaultCachedObject cachedObject = (DefaultCachedObject) delegate.getObject(key);
		if (cachedObject != null) {
			if (!isExpired(cachedObject)) {
				cachedObject.expired = expired;
			}
		}
	}

	public Object getObject(Object key) {
		DefaultCachedObject cachedObject = (DefaultCachedObject) delegate.getObject(key);
		if (cachedObject != null) {
			if (isExpired(cachedObject)) {
				delegate.removeObject(key);
				return null;
			} else {
				return cachedObject.value;
			}
		}
		return null;
	}

	protected boolean isExpired(CachedObject cachedObject) {
		return (cachedObject.getExpired() > 0) && (TimeUnit.MILLISECONDS.convert(cachedObject.getExpired(),
				TimeUnit.SECONDS) > System.currentTimeMillis() - cachedObject.getCreated());
	}

	public Object removeObject(Object key) {
		DefaultCachedObject cachedObject = (DefaultCachedObject) delegate.removeObject(key);
		return cachedObject != null ? cachedObject.value : null;
	}

	public void clear() {
		delegate.clear();
	}

	public int getSize() {
		return delegate.getSize();
	}

	public Set<Object> keys() {
		return delegate.keys();
	}

	public boolean hasKey(Object key) {
		return delegate.hasKey(key);
	}

	public String toString() {
		return delegate.toString();
	}

	public ExpiredCache checked(int interval) {
		return new CheckedExpiredCache(this, interval);
	}

	public void close() {
	}

}
