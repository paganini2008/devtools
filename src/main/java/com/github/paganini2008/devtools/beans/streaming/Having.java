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

import java.util.function.Predicate;

import com.github.paganini2008.devtools.Comparables;

/**
 * 
 * Having
 * <p>
 * Factory of predicates on groups, like sql 'having max(price) &gt; 100'.
 * <p>
 * The aggregation result is compared null-safely like
 * {@link com.github.paganini2008.devtools.Comparables}: a null result (e.g.
 * 'max' or 'avg' of a column whose values are all null, or 'min' of a column
 * containing a null) is regarded as smaller than any value, so it matches
 * 'lt'/'lte'/'ne' but never 'gt'/'gte' (unlike sql where a comparison with
 * null is unknown and never matches). The predicates can be combined with
 * {@link Predicate#and(Predicate)}, {@link Predicate#or(Predicate)} and
 * {@link Predicate#negate()}.
 * 
 * <pre>
 * // select location, count(*) from products group by location having count(*) &gt;= 2
 * Selector.from(products).groupBy("location", String.class).having(Having.gte(Functions.count(), 2));
 * // ... having max(price) &gt; 300 or sum(sales) between 30 and 40
 * Having.gt(Functions.max(Product::getPrice), 300f)
 *     .or(Having.between(Functions.sum("sales"), new BigDecimal("30"), new BigDecimal("40")));
 * </pre>
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public abstract class Having {

	/**
	 * Like sql 'having aggregation = value'.
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> eq(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.eq(result, value);
		};
	}

	/**
	 * Like sql 'having aggregation &lt;&gt; value'.
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> ne(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.ne(result, value);
		};
	}

	/**
	 * Like sql 'having aggregation between minValue and maxValue' (both inclusive).
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> between(Aggregation<E, T> aggregation, T minValue, T maxValue) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.gte(result, minValue) && Comparables.lte(result, maxValue);
		};
	}

	/**
	 * Like sql 'having aggregation &lt; value'.
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> lt(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.lt(result, value);
		};
	}

	/**
	 * Like sql 'having aggregation &lt;= value'.
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> lte(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.lte(result, value);
		};
	}

	/**
	 * Like sql 'having aggregation &gt; value'.
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> gt(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.gt(result, value);
		};
	}

	/**
	 * Like sql 'having aggregation &gt;= value'.
	 */
	public static <E, T extends Comparable<T>> Predicate<Group<E>> gte(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.gte(result, value);
		};
	}

}
