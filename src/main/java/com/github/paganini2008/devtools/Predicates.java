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
package com.github.paganini2008.devtools;

import java.util.Collection;
import java.util.function.Function;

/**
 * 
 * Predicates
 * 
 * Factory methods for {@link FunctionalPredicate}s, e.g.
 * {@code Predicates.<User>junction().gt(User::getAge, 18).nonNull(User::getName)}.
 *
 * @author Fred Feng
 * @date 2022-03-16
 *
 * @version 2.0.5
 */
public abstract class Predicates {

	/**
	 * Returns a new predicate that accepts everything until conditions are added.
	 */
	public static <T> FunctionalPredicate<T> junction() {
		return new FunctionalPredicateImpl<T>();
	}

	/**
	 * Returns a predicate that tests whether the extracted value lies between {@code minValue} and {@code maxValue} (bounds excluded if {@code exclusive}).
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> between(Function<T, R> supplier, R minValue, R maxValue,
			boolean exclusive) {
		return new FunctionalPredicateImpl<T>().between(supplier, minValue, maxValue, exclusive);
	}

	/**
	 * Returns a predicate that tests whether the extracted value equals {@code value}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> eq(Function<T, R> supplier, R value) {
		return new FunctionalPredicateImpl<T>().eq(supplier, value);
	}

	/**
	 * Returns a predicate that tests whether the extracted value does not equal {@code value}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> ne(Function<T, R> supplier, R value) {
		return new FunctionalPredicateImpl<T>().ne(supplier, value);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is less than {@code value}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> lt(Function<T, R> supplier, R value) {
		return new FunctionalPredicateImpl<T>().lt(supplier, value);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is less than or equal to {@code value}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> lte(Function<T, R> supplier, R value) {
		return new FunctionalPredicateImpl<T>().lte(supplier, value);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is greater than {@code value}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> gt(Function<T, R> supplier, R value) {
		return new FunctionalPredicateImpl<T>().gt(supplier, value);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is greater than or equal to {@code value}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> gte(Function<T, R> supplier, R value) {
		return new FunctionalPredicateImpl<T>().gte(supplier, value);
	}

	/**
	 * Returns a predicate that tests whether the extracted value equals any of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> eqAny(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().eqAny(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value equals all of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> eqAll(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().eqAll(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is less than any of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> ltAny(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().ltAny(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is less than all of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> ltAll(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().ltAll(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is less than or equal to any of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> lteAny(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().lteAny(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is less than or equal to all of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> lteAll(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().lteAll(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is greater than any of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> gtAny(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().gtAny(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is greater than all of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> gtAll(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().gtAll(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is greater than or equal to any of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> gteAny(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().gteAny(supplier, values);
	}

	/**
	 * Returns a predicate that tests whether the extracted value is greater than or equal to all of {@code values}.
	 */
	public static <T, R extends Comparable<R>> FunctionalPredicate<T> gteAll(Function<T, R> supplier, Collection<R> values) {
		return new FunctionalPredicateImpl<T>().gteAll(supplier, values);
	}

}
