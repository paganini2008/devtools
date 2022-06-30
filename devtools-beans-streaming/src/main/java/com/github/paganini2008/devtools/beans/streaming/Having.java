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

import java.util.function.Predicate;

import com.github.paganini2008.devtools.Comparables;

/**
 * 
 * Having
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class Having {

	public static <E, T extends Comparable<T>> Predicate<Group<E>> eq(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.eq(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> ne(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.ne(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> between(Aggregation<E, T> aggregation, T minValue, T maxValue) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.gte(result, minValue) && Comparables.lte(result, maxValue);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> lt(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.lt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> lte(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.lte(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> gt(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.gt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> gte(Aggregation<E, T> aggregation, T value) {
		return group -> {
			T result = group.summarize(aggregation);
			return Comparables.gte(result, value);
		};
	}

}
