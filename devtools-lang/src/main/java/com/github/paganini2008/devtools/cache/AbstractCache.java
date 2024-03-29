/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 
 * AbstractCache
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class AbstractCache implements Cache {

	public Cache masterStandbyCache(Cache backup) {
		return new MasterStandbyCache(this, backup);
	}

	public ExpiredCache expiredCache(int interval, TimeUnit timeUnit) {
		if (this instanceof CheckedExpiredCache) {
			throw new UnsupportedOperationException();
		}
		return new CheckedExpiredCache(this, interval, timeUnit);
	}

	public ExpiredCache expiredCache() {
		if (this instanceof ExpiredCache) {
			throw new UnsupportedOperationException();
		}
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
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class CacheIterator implements Iterator<Object> {

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
