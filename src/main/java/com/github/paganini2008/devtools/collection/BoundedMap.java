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
package com.github.paganini2008.devtools.collection;

import java.util.Map;

/**
 * 
 * BoundedMap
 * <p>
 * A map holding a limited number of entries, which evicts entries when the
 * limit is exceeded.
 *
 * @author Fred Feng
 * @date 2021-01-18
 * @since 2.0.1
 */
public interface BoundedMap<K, V> extends Map<K, V> {

	/**
	 * Returns the maximum number of entries.
	 */
	int getMaxSize();

	/**
	 * Returns the underlying map.
	 *
	 * @return the underlying map, or null if not available
	 */
	default Map<K, V> getDelegate() {
		return null;
	}

	/**
	 * Invoked after an entry has been evicted. Does nothing by default.
	 *
	 * @param eldestKey
	 *            the key of the evicted entry
	 * @param eldestValue
	 *            the value of the evicted entry
	 */
	default void onEviction(K eldestKey, V eldestValue) {
	}

}
