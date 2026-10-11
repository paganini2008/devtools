/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.cache;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * UncheckedExpiredCache
 * <p>
 * An {@link ExpiredCache} which checks the expiration lazily when an entry is
 * read; expired entries are removed on access.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public class UncheckedExpiredCache extends AbstractCache implements ExpiredCache {

	// Serializable so that the wrapped entries can be evicted into a SerializableCacheStore
	static class DefaultCachedObject implements CachedObject, Serializable {

		private static final long serialVersionUID = 4507853640384437426L;

		final Object value;
		final long created;
		volatile int expired;

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

	public void putObject(Object key, Object value, boolean ifAbsent) {
		putObject(key, value, ifAbsent, -1);
	}

	public void putObject(Object key, Object value, boolean ifAbsent, int expired) {
		if (ifAbsent) {
			// An expired entry is logically absent, it must not block the new value
			removeIfExpired(key);
		}
		DefaultCachedObject cachedObject = new DefaultCachedObject(value, System.currentTimeMillis(), expired);
		delegate.putObject(key, cachedObject, ifAbsent);
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

	/**
	 * Removes the entry if it has expired, without changing the eviction order
	 * of a {@link BoundedCache} delegate.
	 *
	 * @return true if an expired entry was removed
	 */
	boolean removeIfExpired(Object key) {
		if (key == null) {
			return false;
		}
		Object object = delegate instanceof BoundedCache ? ((BoundedCache) delegate).peekObject(key) : delegate.getObject(key);
		if (object instanceof CachedObject && isExpired((CachedObject) object)) {
			delegate.removeObject(key);
			return true;
		}
		return false;
	}

	protected boolean isExpired(CachedObject cachedObject) {
		return (cachedObject.getExpired() > 0) && (System.currentTimeMillis() - cachedObject.getCreated() >= TimeUnit.MILLISECONDS
				.convert(cachedObject.getExpired(), TimeUnit.SECONDS));
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

	/**
	 * Returns false for an expired entry (which is removed).
	 */
	public boolean hasKey(Object key) {
		return !removeIfExpired(key) && delegate.hasKey(key);
	}

	public String toString() {
		return delegate.toString();
	}

	public void close() {
		delegate.close();
	}

}
