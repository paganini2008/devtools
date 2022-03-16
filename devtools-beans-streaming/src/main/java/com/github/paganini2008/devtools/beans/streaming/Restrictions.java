/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * Restrictions</br>
 * A filter tools for where clause
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class Restrictions {

	public static <E> Restriction<E> junction() {
		return new RestrictionImpl<E>();
	}

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

}
