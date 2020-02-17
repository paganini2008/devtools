package com.github.paganini2008.devtools.beans.streaming;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.github.paganini2008.devtools.CompareUtils;
import com.github.paganini2008.devtools.comparator.AbstractComparator;
import com.github.paganini2008.devtools.comparator.ReverseComparator;

/**
 * 
 * BeanSort
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
@SuppressWarnings("all")
public class BeanSort<E> extends AbstractComparator<E> implements Sort<E> {

	/**
	 * 
	 * ComparatorChain
	 * 
	 * @author Fred Feng
	 * 
	 * @version 1.0
	 */
	static class ComparatorChain<E, T extends Comparable<T>> implements Comparator<E> {

		ComparatorChain(Function<E, T> function) {
			this.function = function;
		}

		private final Function<E, T> function;

		public int compare(E a, E b) {
			T left = function.apply(a);
			T right = function.apply(b);
			return CompareUtils.compareTo(left, right);
		}

		public Comparator reverse() {
			return new ReverseComparator<E>(this);
		}

	}

	private final List<Comparator<E>> comparatorChains = new ArrayList<Comparator<E>>();

	public BeanSort<E> reset() {
		comparatorChains.clear();
		return this;
	}

	public <T extends Comparable<T>> BeanSort<E> ascending(Function<E, T> function) {
		comparatorChains.add(new ComparatorChain(function));
		return this;
	}

	public <T extends Comparable<T>> BeanSort<E> descending(Function<E, T> function) {
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
