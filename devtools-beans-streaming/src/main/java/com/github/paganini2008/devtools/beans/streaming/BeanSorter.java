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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.comparator.AbstractComparator;
import com.github.paganini2008.devtools.comparator.ReverseComparator;

/**
 * 
 * BeanSorter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("all")
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

		public Comparator reverse() {
			return new ReverseComparator<E>(this);
		}

	}

	private final List<Comparator<E>> comparatorChains = new ArrayList<Comparator<E>>();

	public BeanSorter<E> reset() {
		comparatorChains.clear();
		return this;
	}

	public <T extends Comparable<T>> BeanSorter<E> ascending(Function<E, T> function) {
		comparatorChains.add(new ComparatorChain(function));
		return this;
	}

	public <T extends Comparable<T>> BeanSorter<E> descending(Function<E, T> function) {
		comparatorChains.add(new ComparatorChain(function).reverse());
		return this;
	}

	public int compare(E a, E b) {
		int value = 0;
		for (Comparator<E> chain : comparatorChains) {
			if ((value = chain.compare(a, b)) != 0) {
				break;
			}
		}
		return value;
	}

}
