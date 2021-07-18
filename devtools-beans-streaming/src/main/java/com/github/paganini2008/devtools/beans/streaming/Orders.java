/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.function.Function;

/**
 * 
 * Orders
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public abstract class Orders {

	public static <E, T extends Comparable<T>> Sorter<E> descending(final String attributeName, final Class<T> requiredType) {
		return descending(Property.forName(attributeName, requiredType));
	}

	public static <E, T extends Comparable<T>> Sorter<E> ascending(final String attributeName, final Class<T> requiredType) {
		return ascending(Property.forName(attributeName, requiredType));
	}

	public static <E, T extends Comparable<T>> Sorter<E> descending(final Function<E, T> function) {
		BeanSorter<E> sort = new BeanSorter<E>();
		sort.descending(function);
		return sort;
	}

	public static <E, T extends Comparable<T>> Sorter<E> ascending(final Function<E, T> function) {
		BeanSorter<E> sort = new BeanSorter<E>();
		sort.ascending(function);
		return sort;
	}

	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupAscending(final String attributeName, final Class<T> requiredType) {
		return groupAscending(group -> {
			return MappedBy.forName(attributeName, requiredType).apply(group.getAttributes());
		});
	}

	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupDescending(final String attributeName, final Class<T> requiredType) {
		return groupDescending(group -> {
			return MappedBy.forName(attributeName, requiredType).apply(group.getAttributes());
		});
	}

	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupDescending(final GroupFunction<E, T> function) {
		BeanSorter<Group<E>> sort = new BeanSorter<Group<E>>();
		sort.descending(function);
		return sort;
	}

	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupAscending(final GroupFunction<E, T> function) {
		BeanSorter<Group<E>> sort = new BeanSorter<Group<E>>();
		sort.ascending(function);
		return sort;
	}
}
