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

import java.util.Comparator;
import java.util.function.Function;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.comparator.AbstractComparator;
import com.github.paganini2008.devtools.comparator.ComparatorHelper;

/**
 * 
 * BeanSorter
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * @since 2.0.1
 */
public class BeanSorter<E> extends AbstractComparator<E> implements Sorter<E> {

	/**
	 * 
	 * ComparatorChain
	 * 
	 * @author Fred Feng
	 * 
	 * @since 2.0.1
	 */
	static class ComparatorChain<E, T extends Comparable<T>> implements Comparator<E> {

		ComparatorChain(Function<E, T> function) {
			this.function = function;
		}

		private final Function<E, T> function;

		public int compare(E a, E b) {
			T left = function.apply(a);
			T right = function.apply(b);
			return Comparables.compareTo(left, right);
		}

	}

	private Comparator<E> comparator = ComparatorHelper.identity();

	/**
	 * Clears all the sort keys, after which all elements are regarded as equal.
	 */
	public BeanSorter<E> reset() {
		this.comparator = ComparatorHelper.identity();
		return this;
	}

	/**
	 * Appends an ascending sort key.
	 */
	public <T extends Comparable<T>> BeanSorter<E> ascending(Function<E, T> function) {
		this.comparator = comparator.thenComparing(new ComparatorChain<>(function));
		return this;
	}

	/**
	 * Appends a descending sort key.
	 */
	public <T extends Comparable<T>> BeanSorter<E> descending(Function<E, T> function) {
		this.comparator = comparator.thenComparing(new ComparatorChain<>(function).reversed());
		return this;
	}

	public int compare(E a, E b) {
		return this.comparator.compare(a, b);
	}

}
