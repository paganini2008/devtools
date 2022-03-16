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
package com.github.paganini2008.devtools;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 
 * FunctionalPredicate
 *
 * @author Fred Feng
 *
 * @version 1.1.0
 */
public interface FunctionalPredicate<T> extends Predicate<T> {

	<R extends Comparable<R>> FunctionalPredicate<T> nonNull(Function<T, R> supplier);

	<R extends Comparable<R>> FunctionalPredicate<T> isNull(Function<T, R> supplier);

	<R extends Comparable<R>> FunctionalPredicate<T> between(Function<T, R> supplier, R minValue, R maxValue, boolean exclusive);

	<R extends Comparable<R>> FunctionalPredicate<T> eq(Function<T, R> supplier, R value);

	<R extends Comparable<R>> FunctionalPredicate<T> ne(Function<T, R> supplier, R value);

	<R extends Comparable<R>> FunctionalPredicate<T> lt(Function<T, R> supplier, R value);

	<R extends Comparable<R>> FunctionalPredicate<T> lte(Function<T, R> supplier, R value);

	<R extends Comparable<R>> FunctionalPredicate<T> gt(Function<T, R> supplier, R value);

	<R extends Comparable<R>> FunctionalPredicate<T> gte(Function<T, R> supplier, R value);

	<R extends Comparable<R>> FunctionalPredicate<T> eqAny(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> eqAll(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> ltAny(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> ltAll(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> lteAny(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> lteAll(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> gtAny(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> gtAll(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> gteAny(Function<T, R> supplier, Collection<R> values);

	<R extends Comparable<R>> FunctionalPredicate<T> gteAll(Function<T, R> supplier, Collection<R> values);

	FunctionalPredicate<T> matches(Function<T, String> supplier, String substr, MatchMode matchMode);

}
