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
 * LruMapSupplier
 * <p>
 * Creates the access-ordered map used by the Lru* collections to track the
 * usage of their elements.
 *
 * @author Fred Feng
 * @date 2021-01-18
 * @since 2.0.1
 */
@FunctionalInterface
public interface LruMapSupplier<K, V> {

	/**
	 * Creates an access-ordered map which drops its least recently used entry when
	 * the size exceeds <code>maxSize</code>.
	 *
	 * @param maxSize
	 *            the maximum number of entries
	 * @param evictionListener
	 *            must be notified of every dropped entry
	 * @return the new map
	 */
	Map<K, V> get(int maxSize, EvictionListener<K, V> evictionListener);

}
