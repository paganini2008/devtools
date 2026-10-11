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
 * Groupable
 * <p>
 * The grouping stage of an in-memory query, supporting nested 'group by',
 * 'having' and 'order by' on groups.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public interface Groupable<E> {

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
	 * Groups by a bean property, divides every current group further.
	 */
	default <T> Groupable<E> groupBy(String attributeName, Class<T> requiredType) {
		return groupBy(Property.forName(attributeName, requiredType), attributeName);
	}

	/**
	 * Groups by the value of the function, which is stored in the group attributes under the given
	 * name. Divides every current group further.
	 */
	<T> Groupable<E> groupBy(Function<E, T> function, String attributeName);

	/**
	 * Keeps only the groups matching the predicate, like sql 'having'. See {@link Having}. The groups are filtered
	 * immediately, so successive calls are combined with 'and' (a later call never brings back a group removed by an
	 * earlier one), and a nested 'group by' after it only divides the remaining groups.
	 * 
	 * <pre>
	 * // select location, count(*) from products group by location having count(*) &gt;= 2 and sum(sales) &gt; 30
	 * Selector.from(products).groupBy("location", String.class)
	 *     .having(Having.gte(Functions.count(), 2))
	 *     .having(Having.gt(Functions.sum("sales"), new BigDecimal("30")));
	 * </pre>
	 */
	Groupable<E> having(Predicate<Group<E>> predicate);

	/**
	 * Sorts the groups. See {@link Orders#groupAscending(String, Class)}.
	 */
	Groupable<E> orderBy(Sorter<Group<E>> sort);

	/**
	 * Ends the query, every group is transformed into a result row (see {@link View}) on paging.
	 */
	<T> ResultSetSlice<T> setTransformer(Transformer<Group<E>, T> transformer);

}
