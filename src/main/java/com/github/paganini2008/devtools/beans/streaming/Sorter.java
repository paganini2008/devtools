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
package com.github.paganini2008.devtools.beans.streaming;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 
 * Sorter
 * <p>
 * A chainable comparator, like sql 'order by a asc, b desc'.
 * 
 * @author Fred Feng
 * @date 2021-01-18
 * @since 2.0.1
 */
public interface Sorter<E> extends Comparator<E> {

	/**
	 * Appends an ascending sort key on a bean property.
	 */
	default <T extends Comparable<T>> Sorter<E> ascending(String attributeName, Class<T> requiredType) {
		return ascending(Property.forName(attributeName, requiredType));
	}

	/**
	 * Appends an ascending sort key.
	 */
	<T extends Comparable<T>> Sorter<E> ascending(Function<E, T> function);

	/**
	 * Appends a descending sort key on a bean property.
	 */
	default <T extends Comparable<T>> Sorter<E> descending(String attributeName, Class<T> requiredType) {
		return descending(Property.forName(attributeName, requiredType));
	}

	/**
	 * Appends a descending sort key.
	 */
	<T extends Comparable<T>> Sorter<E> descending(Function<E, T> function);
}
