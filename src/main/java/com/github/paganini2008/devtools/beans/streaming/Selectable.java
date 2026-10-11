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
import java.util.function.Predicate;

import com.github.paganini2008.devtools.beans.LambdaUtils;
import com.github.paganini2008.devtools.beans.SerializableFunction;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Selectable
 * <p>
 * The selecting stage of an in-memory query over a collection of beans,
 * supporting 'where', 'distinct', 'order by', 'group by' and paging.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public interface Selectable<E> extends ResultSetSlice<E> {

	/**
	 * Keeps only the elements matching the predicate, like sql 'where'. See {@link Restrictions}.
	 */
	Selectable<E> filter(Predicate<E> predicate);

	/**
	 * Sorts the elements (stable), like sql 'order by'. See {@link Orders}.
	 */
	Selectable<E> orderBy(Sorter<E> sort);

	/**
	 * Removes the duplicated elements (by equals), keeping the first occurrence.
	 */
	Selectable<E> distinct();

	/**
	 * Starts grouping by the value of the function, stored in the group attributes under the alias.
	 */
	<T> Groupable<E> groupBy(Function<E, T> function, String alias);

	/**
	 * Groups by a getter method reference, the resolved property name (see
	 * {@link LambdaUtils#getPropertyName(SerializableFunction)}) is used as the
	 * attribute name of the groups (and so the key in the {@link View} tuples).
	 * The getter is invoked directly to get the values.
	 * 
	 * <pre>
	 * // select location, count(*) from products group by location
	 * Selector.from(products).groupBy(Product::getLocation).setTransformer(new View&lt;Product&gt;() {
	 * 	protected void setAttributes(Tuple tuple, Group&lt;Product&gt; group) {
	 * 		tuple.set("count", group.count());
	 * 	}
	 * }).list(); // tuples like {location=London, count=3}
	 * </pre>
	 * 
	 * @throws IllegalArgumentException if the argument is not a getter method
	 *             reference (e.g. an ordinary lambda)
	 * @since 3.0.0
	 */
	default <T> Groupable<E> groupBy(SerializableFunction<E, T> getter) {
		return groupBy(getter, LambdaUtils.getPropertyName(getter));
	}

	/**
	 * Starts grouping by a bean property.
	 */
	default <T> Groupable<E> groupBy(String attributeName, Class<T> requiredType) {
		return groupBy(Property.forName(attributeName, requiredType), attributeName);
	}

	/**
	 * Ends the query, every element is transformed into a result row on paging.
	 */
	<T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer);

}
