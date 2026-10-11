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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 
 * AbstractCache
 * <p>
 * Base class of caches, providing iteration and the factory methods to
 * decorate this cache with expiration or a backup cache.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public abstract class AbstractCache implements Cache {

	/**
	 * Returns a two-level cache with this cache as the master. See {@link MasterStandbyCache}.
	 */
	public Cache masterStandbyCache(Cache backup) {
		return new MasterStandbyCache(this, backup);
	}

	/**
	 * Returns an expired cache based on this cache, which purges the expired entries every
	 * interval.
	 * @throws UnsupportedOperationException if this cache is already a {@link CheckedExpiredCache}
	 */
	public ExpiredCache expiredCache(int interval, TimeUnit timeUnit) {
		if (this instanceof CheckedExpiredCache) {
			throw new UnsupportedOperationException();
		}
		return new CheckedExpiredCache(this, interval, timeUnit);
	}

	/**
	 * Returns an expired cache based on this cache, which checks the expiration on access.
	 * @throws UnsupportedOperationException if this cache is already an {@link ExpiredCache}
	 */
	public ExpiredCache expiredCache() {
		if (this instanceof ExpiredCache) {
			throw new UnsupportedOperationException();
		}
		return new UncheckedExpiredCache(this);
	}

	/**
	 * Iterates over a snapshot of the keys and returns their values. {@link Iterator#remove()}
	 * removes the entry from this cache.
	 */
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
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class CacheIterator implements Iterator<Object> {

		private final Cache delegate;
		private final Iterator<Object> it;
		private Object key;

		CacheIterator(Cache delegate) {
			this.delegate = delegate;
			// Iterate over a snapshot so that removing entries is always safe
			this.it = new ArrayList<Object>(delegate.keys()).iterator();
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public Object next() {
			key = it.next();
			return delegate.getObject(key);
		}

		public void remove() {
			if (key == null) {
				throw new IllegalStateException();
			}
			delegate.removeObject(key);
			key = null;
		}

	}
}
