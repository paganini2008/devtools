/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.FunctionalPredicate;
import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * Restriction
 *
 * @author Fred Feng
 *
 * @version 2.0.5
 */
public interface Restriction<T> extends FunctionalPredicate<T> {

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

}
