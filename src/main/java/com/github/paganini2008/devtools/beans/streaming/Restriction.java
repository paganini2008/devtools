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

import java.util.Collection;
import java.util.function.Function;

import com.github.paganini2008.devtools.FunctionalPredicate;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.SerializableFunction;

/**
 * 
 * Restriction
 * <p>
 * A chainable predicate on bean properties, used as the 'where' clause of an
 * in-memory query. All the conditions are combined with 'and'.
 *
 * @author Fred Feng
 * @date 2022-03-16
 *
 * @version 2.0.5
 */
public interface Restriction<T> extends FunctionalPredicate<T> {

	/**
	 * Conditions on the (nested) property 'attributeName'. The variants with a 'requiredType'
	 * convert the property value to that type first (a value that cannot be converted is regarded
	 * as null), the others compare the raw value. For a nested name like "owner.address.city" a
	 * null intermediate bean reads as null (like a sql outer join) instead of failing.
	 * <p>
	 * Note: like {@link com.github.paganini2008.devtools.Comparables}, a null property value is
	 * regarded as smaller than any non-null value. The '*Any' conditions match if the condition
	 * holds for any of the values, the '*All' ones if it holds for all of them; both never match an
	 * empty collection.
	 */
	<R extends Comparable<R>> Restriction<T> nonNull(String attributeName);

	<R extends Comparable<R>> Restriction<T> isNull(String attributeName);

	<R extends Comparable<R>> Restriction<T> between(String attributeName, R minValue, R maxValue, boolean exclusive);

	<R extends Comparable<R>> Restriction<T> between(String attributeName, Class<R> requiredType, R minValue, R maxValue,
			boolean exclusive);

	<R extends Comparable<R>> Restriction<T> eq(String attributeName, R value);

	<R extends Comparable<R>> Restriction<T> eq(String attributeName, Class<R> requiredType, R value);

	<R extends Comparable<R>> Restriction<T> ne(String attributeName, R value);

	<R extends Comparable<R>> Restriction<T> ne(String attributeName, Class<R> requiredType, R value);

	<R extends Comparable<R>> Restriction<T> lt(String attributeName, R value);

	<R extends Comparable<R>> Restriction<T> lt(String attributeName, Class<R> requiredType, R value);

	<R extends Comparable<R>> Restriction<T> lte(String attributeName, R value);

	<R extends Comparable<R>> Restriction<T> lte(String attributeName, Class<R> requiredType, R value);

	<R extends Comparable<R>> Restriction<T> gt(String attributeName, Class<R> requiredType, R value);

	<R extends Comparable<R>> Restriction<T> gt(String attributeName, R value);

	<R extends Comparable<R>> Restriction<T> gte(String attributeName, Class<R> requiredType, R value);

	<R extends Comparable<R>> Restriction<T> gte(String attributeName, R value);

	<R extends Comparable<R>> Restriction<T> eqAny(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> eqAny(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> eqAll(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> eqAll(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> ltAny(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> ltAny(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> ltAll(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> ltAll(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> lteAny(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> lteAny(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> lteAll(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> lteAll(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gtAny(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gtAny(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gtAll(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gtAll(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gteAny(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gteAny(String attributeName, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gteAll(String attributeName, Class<R> requiredType, Collection<R> values);

	<R extends Comparable<R>> Restriction<T> gteAll(String attributeName, Collection<R> values);

	Restriction<T> matches(String attributeName, String substr, MatchMode matchMode);

	// ---------------------------------------------------------------- getter method references (since 3.0.0)

	/**
	 * Like {@link #nonNull(String)}, reading the value by a getter method reference instead of a property name. The
	 * getter is invoked directly (no reflection), so any serializable function works.
	 * 
	 * <pre>
	 * Restrictions.&lt;Product&gt;junction().nonNull(Product::getFreight).gt(Product::getPrice, 100f)
	 * </pre>
	 * 
	 * The other overloads taking a {@link SerializableFunction} follow the same rules and have exactly the same
	 * semantics (null handling, comparison) as the corresponding overloads taking a property name.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> nonNull(SerializableFunction<T, R> getter) {
		nonNull((Function<T, R>) getter);
		return this;
	}

	/**
	 * Like {@link #isNull(String)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> isNull(SerializableFunction<T, R> getter) {
		isNull((Function<T, R>) getter);
		return this;
	}

	/**
	 * Like {@link #between(String, Comparable, Comparable, boolean)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> between(SerializableFunction<T, R> getter, R minValue, R maxValue,
			boolean exclusive) {
		between((Function<T, R>) getter, minValue, maxValue, exclusive);
		return this;
	}

	/**
	 * Like {@link #eq(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> eq(SerializableFunction<T, R> getter, R value) {
		eq((Function<T, R>) getter, value);
		return this;
	}

	/**
	 * Like {@link #ne(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> ne(SerializableFunction<T, R> getter, R value) {
		ne((Function<T, R>) getter, value);
		return this;
	}

	/**
	 * Like {@link #lt(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> lt(SerializableFunction<T, R> getter, R value) {
		lt((Function<T, R>) getter, value);
		return this;
	}

	/**
	 * Like {@link #lte(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> lte(SerializableFunction<T, R> getter, R value) {
		lte((Function<T, R>) getter, value);
		return this;
	}

	/**
	 * Like {@link #gt(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> gt(SerializableFunction<T, R> getter, R value) {
		gt((Function<T, R>) getter, value);
		return this;
	}

	/**
	 * Like {@link #gte(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> gte(SerializableFunction<T, R> getter, R value) {
		gte((Function<T, R>) getter, value);
		return this;
	}

	/**
	 * Like {@link #eqAny(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> eqAny(SerializableFunction<T, R> getter, Collection<R> values) {
		eqAny((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #eqAll(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> eqAll(SerializableFunction<T, R> getter, Collection<R> values) {
		eqAll((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #ltAny(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> ltAny(SerializableFunction<T, R> getter, Collection<R> values) {
		ltAny((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #ltAll(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> ltAll(SerializableFunction<T, R> getter, Collection<R> values) {
		ltAll((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #lteAny(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> lteAny(SerializableFunction<T, R> getter, Collection<R> values) {
		lteAny((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #lteAll(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> lteAll(SerializableFunction<T, R> getter, Collection<R> values) {
		lteAll((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #gtAny(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> gtAny(SerializableFunction<T, R> getter, Collection<R> values) {
		gtAny((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #gtAll(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> gtAll(SerializableFunction<T, R> getter, Collection<R> values) {
		gtAll((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #gteAny(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> gteAny(SerializableFunction<T, R> getter, Collection<R> values) {
		gteAny((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #gteAll(String, Collection)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default <R extends Comparable<R>> Restriction<T> gteAll(SerializableFunction<T, R> getter, Collection<R> values) {
		gteAll((Function<T, R>) getter, values);
		return this;
	}

	/**
	 * Like {@link #matches(String, String, MatchMode)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	default Restriction<T> matches(SerializableFunction<T, String> getter, String substr, MatchMode matchMode) {
		matches((Function<T, String>) getter, substr, matchMode);
		return this;
	}

}
