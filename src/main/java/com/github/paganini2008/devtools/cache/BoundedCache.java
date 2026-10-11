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

/**
 * 
 * BoundedCache
 * <p>
 * Base class of caches with a maximum size. Evicted entries are written to the
 * {@link CacheStore} if one is set, otherwise passed to {@link
 * #dispose(Object, Object)}.
 * 
 * @author Fred Feng
 * @date 2021-01-18
 * @since 2.0.1
 */
public abstract class BoundedCache extends AbstractCache implements Cache {

	/**
	 * Sets the secondary storage of the evicted entries.
	 */
	public abstract void setCacheStore(CacheStore store);

	/**
	 * Called when an entry is evicted without a cache store, or removed. Does nothing by default.
	 */
	protected void dispose(Object eldestKey, Object eldestObject) {
	}

	/**
	 * Returns the value of the key without changing the eviction order and
	 * without moving an entry back from the cache store. Used by the expiration
	 * check of {@link CheckedExpiredCache}, which must not disturb the order.
	 */
	Object peekObject(Object key) {
		return getObject(key);
	}

}
