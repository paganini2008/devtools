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

import java.util.Set;

/**
 * CacheStore
 * <p>
 * A secondary storage (e.g. disk) holding the entries evicted from a {@link
 * BoundedCache}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public interface CacheStore {

	/**
	 * Stores an evicted entry.
	 */
	void writeObject(Object key, Object eldestObject);

	/**
	 * Reads a stored value, or null if absent.
	 */
	Object readObject(Object key);

	/**
	 * Removes a stored entry and returns its value, or null if absent.
	 */
	Object removeObject(Object key);

	/**
	 * Returns the number of stored entries.
	 */
	int getSize();

	/**
	 * Returns the stored keys.
	 */
	Set<Object> keys();

	/**
	 * Returns true if the key is stored.
	 */
	boolean hasKey(Object key);

}
