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

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.SerializableFunction;

/**
 * 
 * Restrictions</br>
 * A filter tools for where clause. A property can be given by name (nested
 * names like "salesman.name" are supported, a null intermediate bean reads as
 * null) or, since 3.0.0, by a getter method reference:
 * 
 * <pre>
 * // where location = 'London' and price &gt; 700
 * Restrictions.eq("location", "London").and(Restrictions.gt("price", 700f))
 * Restrictions.eq(Product::getLocation, "London").and(Restrictions.gt(Product::getPrice, 700f))
 * </pre>
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public abstract class Restrictions {

	/**
	 * Returns an empty restriction matching everything, to which conditions can be chained.
	 */
	public static <E> Restriction<E> junction() {
		return new RestrictionImpl<E>();
	}

	/**
	 * Like sql 'where attributeName &lt;&gt; value'. The other factory methods are named after the
	 * sql operators the same way; the variants with a 'requiredType' convert the property value to
	 * that type before comparing.
	 */
	public static <E, T extends Comparable<T>> Restriction<E> ne(String attributeName, T value) {
		return new RestrictionImpl<E>().ne(attributeName, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> ne(String attributeName, Class<T> requiredType, T value) {
		return new RestrictionImpl<E>().ne(attributeName, requiredType, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> eq(String attributeName, T value) {
		return new RestrictionImpl<E>().eq(attributeName, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> eq(String attributeName, Class<T> requiredType, T value) {
		return new RestrictionImpl<E>().eq(attributeName, requiredType, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> gte(String attributeName, T value) {
		return new RestrictionImpl<E>().gte(attributeName, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> gte(String attributeName, Class<T> requiredType, T value) {
		return new RestrictionImpl<E>().gte(attributeName, requiredType, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> gt(String attributeName, T value) {
		return new RestrictionImpl<E>().gt(attributeName, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> gt(String attributeName, Class<T> requiredType, T value) {
		return new RestrictionImpl<E>().gt(attributeName, requiredType, value);
	}

	public static <E, T extends Comparable<T>> Restriction<E> lte(String attributeName, T value) {
		return new RestrictionImpl<E>().lte(attributeName, value);
	}

	public static <E, T extends Comparable<T>> Predicate<E> lte(String attributeName, Class<T> requiredType, T value) {
		return new RestrictionImpl<E>().lte(attributeName, requiredType, value);
	}

	public static <E, T extends Comparable<T>> Predicate<E> lt(String attributeName, T value) {
		return new RestrictionImpl<E>().lt(attributeName, value);
	}

	public static <E, T extends Comparable<T>> Predicate<E> lt(String attributeName, Class<T> requiredType, T value) {
		return new RestrictionImpl<E>().lt(attributeName, requiredType, value);
	}

	public static <E, T extends Comparable<T>> Predicate<E> between(String attributeName, T minValue, T maxValue, boolean exclusive) {
		return new RestrictionImpl<E>().between(attributeName, minValue, maxValue, exclusive);
	}

	public static <E> Predicate<E> nonNull(String attributeName) {
		return new RestrictionImpl<E>().nonNull(attributeName);
	}

	public static <E> Predicate<E> isNull(String attributeName) {
		return new RestrictionImpl<E>().isNull(attributeName);
	}

	public static <E> Predicate<E> matches(String attributeName, String substr, MatchMode matchMode) {
		return new RestrictionImpl<E>().matches(attributeName, substr, matchMode);
	}

	// ---------------------------------------------------------------- getter method references (since 3.0.0)

	/**
	 * Like {@link #eq(String, Comparable)}, reading the value by a getter method reference instead of a property
	 * name. The getter is invoked directly (type safe, no reflection) and the semantics (null handling, comparison)
	 * are the same as the overloads taking a property name. The other factory methods taking a
	 * {@link SerializableFunction} follow the same rules.
	 * 
	 * <pre>
	 * // where location = 'London' and price &gt; 700
	 * Selector.from(products)
	 *     .filter(Restrictions.eq(Product::getLocation, "London").and(Restrictions.gt(Product::getPrice, 700f)))
	 *     .list();
	 * </pre>
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> eq(SerializableFunction<E, T> getter, T value) {
		return new RestrictionImpl<E>().eq(getter, value);
	}

	/**
	 * Like {@link #ne(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> ne(SerializableFunction<E, T> getter, T value) {
		return new RestrictionImpl<E>().ne(getter, value);
	}

	/**
	 * Like {@link #gt(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> gt(SerializableFunction<E, T> getter, T value) {
		return new RestrictionImpl<E>().gt(getter, value);
	}

	/**
	 * Like {@link #gte(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> gte(SerializableFunction<E, T> getter, T value) {
		return new RestrictionImpl<E>().gte(getter, value);
	}

	/**
	 * Like {@link #lt(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> lt(SerializableFunction<E, T> getter, T value) {
		return new RestrictionImpl<E>().lt(getter, value);
	}

	/**
	 * Like {@link #lte(String, Comparable)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> lte(SerializableFunction<E, T> getter, T value) {
		return new RestrictionImpl<E>().lte(getter, value);
	}

	/**
	 * Like {@link #between(String, Comparable, Comparable, boolean)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T extends Comparable<T>> Restriction<E> between(SerializableFunction<E, T> getter, T minValue, T maxValue,
			boolean exclusive) {
		return new RestrictionImpl<E>().between(getter, minValue, maxValue, exclusive);
	}

	/**
	 * Like {@link #nonNull(String)}, reading the value by a getter. The value needs not be {@link Comparable}.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T> Restriction<E> nonNull(SerializableFunction<E, T> getter) {
		Assert.isNull(getter, "Getter must not be null.");
		return new RestrictionImpl<E>(e -> getter.apply(e) != null);
	}

	/**
	 * Like {@link #isNull(String)}, reading the value by a getter. The value needs not be {@link Comparable}.
	 * 
	 * @since 3.0.0
	 */
	public static <E, T> Restriction<E> isNull(SerializableFunction<E, T> getter) {
		Assert.isNull(getter, "Getter must not be null.");
		return new RestrictionImpl<E>(e -> getter.apply(e) == null);
	}

	/**
	 * Like {@link #matches(String, String, MatchMode)}, reading the value by a getter.
	 * 
	 * @since 3.0.0
	 */
	public static <E> Restriction<E> matches(SerializableFunction<E, String> getter, String substr, MatchMode matchMode) {
		return new RestrictionImpl<E>().matches(getter, substr, matchMode);
	}

}
