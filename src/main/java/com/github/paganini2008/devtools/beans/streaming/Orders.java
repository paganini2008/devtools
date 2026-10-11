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

import java.util.function.Function;

import com.github.paganini2008.devtools.beans.LambdaUtils;
import com.github.paganini2008.devtools.beans.SerializableFunction;

/**
 * 
 * Orders
 * <p>
 * Factory of {@link Sorter sorters} for elements and groups, like sql 'order
 * by'.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public abstract class Orders {

	/**
	 * Sorts by a bean property in descending order (nulls last).
	 */
	public static <E, T extends Comparable<T>> Sorter<E> descending(final String attributeName, final Class<T> requiredType) {
		return descending(Property.forName(attributeName, requiredType));
	}

	/**
	 * Sorts by a bean property in ascending order (nulls first).
	 */
	public static <E, T extends Comparable<T>> Sorter<E> ascending(final String attributeName, final Class<T> requiredType) {
		return ascending(Property.forName(attributeName, requiredType));
	}

	/**
	 * Sorts by the value of the function in descending order (nulls last). A getter method reference works as well:
	 * 
	 * <pre>
	 * // order by price desc, freight asc
	 * Orders.descending(Product::getPrice).ascending(Product::getFreight)
	 * </pre>
	 */
	public static <E, T extends Comparable<T>> Sorter<E> descending(final Function<E, T> function) {
		BeanSorter<E> sort = new BeanSorter<E>();
		sort.descending(function);
		return sort;
	}

	/**
	 * Sorts by the value of the function in ascending order (nulls first).
	 */
	public static <E, T extends Comparable<T>> Sorter<E> ascending(final Function<E, T> function) {
		BeanSorter<E> sort = new BeanSorter<E>();
		sort.ascending(function);
		return sort;
	}

	/**
	 * Sorts groups by a 'group by' attribute in ascending order.
	 */
	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupAscending(final String attributeName, final Class<T> requiredType) {
		return groupAscending(group -> {
			return Mapping.forName(attributeName, requiredType).apply(group.getAttributes());
		});
	}

	/**
	 * Sorts groups by a 'group by' attribute in descending order.
	 */
	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupDescending(final String attributeName, final Class<T> requiredType) {
		return groupDescending(group -> {
			return Mapping.forName(attributeName, requiredType).apply(group.getAttributes());
		});
	}

	/**
	 * Sorts groups by the value of the function (e.g. an aggregation) in descending order.
	 */
	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupDescending(final GroupFunction<E, T> function) {
		BeanSorter<Group<E>> sort = new BeanSorter<Group<E>>();
		sort.descending(function);
		return sort;
	}

	/**
	 * Sorts groups by the value of the function (e.g. an aggregation) in ascending order.
	 */
	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupAscending(final GroupFunction<E, T> function) {
		BeanSorter<Group<E>> sort = new BeanSorter<Group<E>>();
		sort.ascending(function);
		return sort;
	}

	/**
	 * Sorts groups in ascending order by a 'group by' attribute given as a getter method reference; the groups must
	 * have been grouped by the same getter (see {@link Groupable#groupBy(SerializableFunction)}), so the attribute
	 * values are of the getter's type. Null attribute values come first.
	 * <p>
	 * Note: it is not named 'groupAscending' because an overload taking another functional interface would make the
	 * existing calls like {@code groupAscending(group -> group.count())} ambiguous.
	 * 
	 * <pre>
	 * // select location, count(*) from products group by location order by location
	 * Selector.from(products).groupBy(Product::getLocation).orderBy(Orders.groupAscendingBy(Product::getLocation));
	 * </pre>
	 * 
	 * @throws IllegalArgumentException if the argument is not a getter method reference
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupAscendingBy(final SerializableFunction<E, T> getter) {
		final String attributeName = LambdaUtils.getPropertyName(getter);
		return groupAscending(group -> attributeValue(group, attributeName));
	}

	/**
	 * Sorts groups in descending order by a 'group by' attribute given as a getter method reference, see
	 * {@link #groupAscendingBy(SerializableFunction)}. Null attribute values come last.
	 * 
	 * <pre>
	 * Selector.from(products).groupBy(Product::getLocation).orderBy(Orders.groupDescendingBy(Product::getLocation));
	 * </pre>
	 * 
	 * @throws IllegalArgumentException if the argument is not a getter method reference
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Sorter<Group<E>> groupDescendingBy(final SerializableFunction<E, T> getter) {
		final String attributeName = LambdaUtils.getPropertyName(getter);
		return groupDescending(group -> attributeValue(group, attributeName));
	}

	@SuppressWarnings("unchecked")
	private static <E, T> T attributeValue(Group<E> group, String attributeName) {
		return (T) group.getAttributes().get(attributeName);
	}
}
