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

import java.util.Collection;

/**
 * 
 * BoundedCollection
 * <p>
 * A collection holding a limited number of elements, which evicts elements when
 * the limit is exceeded.
 *
 * @author Fred Feng
 * @date 2021-01-18
 * @since 2.0.1
 */
public interface BoundedCollection<E> extends Collection<E> {

	/**
	 * Returns the underlying collection.
	 *
	 * @return the underlying collection, or null if not available
	 */
	default Collection<E> getDelegate() {
		return null;
	}

	/**
	 * Invoked after an element has been evicted. Does nothing by default.
	 *
	 * @param element
	 *            the evicted element
	 */
	default void onEviction(E element) {
	}

}
