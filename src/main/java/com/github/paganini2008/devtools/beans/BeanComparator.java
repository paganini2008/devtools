package com.github.paganini2008.devtools.beans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.comparator.AbstractComparator;
import com.github.paganini2008.devtools.comparator.ReverseComparator;

/**
 * Compare and sort each item in the java.utils.List. The elements can make up
 * with POJO,java.utils.Map. You also can defined other type.
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class BeanComparator<E> extends AbstractComparator<E> {

	/**
	 * ComparatorChain
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class ComparatorChain<E, T extends Comparable<T>> implements Comparator<E> {

		private ComparatorChain(Getter<E, T> getter) {
			this.getter = getter;
		}

		private final Getter<E, T> getter;

		public int compare(E a, E b) {
			T left = getter.apply(a);
			T right = getter.apply(b);
			return Comparables.compareTo(left, right);
		}

		public Comparator reverse() {
			return new ReverseComparator<E>(this);
		}

		public String toString() {
			return "[ComparatorChain] Order by: " + getter;
		}

	}

	private final List<Comparator<E>> comparatorChains = new ArrayList<Comparator<E>>();

	public BeanComparator<E> reset() {
		comparatorChains.clear();
		return this;
	}

	public BeanComparator<E> asc(String propertyName) {
		return asc(propertyName, null);
	}

	public <T extends Comparable<T>> BeanComparator<E> asc(String propertyName, Class<T> requiredType) {
		Property<E, T> property = Property.forName(propertyName, requiredType);
		return asc(property);
	}

	public <T extends Comparable<T>> BeanComparator<E> asc(Getter<E, T> getter) {
		comparatorChains.add(new ComparatorChain(getter));
		return this;
	}

	public BeanComparator<E> desc(String propertyName) {
		return desc(propertyName, null);
	}

	public <T extends Comparable<T>> BeanComparator<E> desc(String propertyName, Class<T> requiredType) {
		Property<E, T> property = Property.forName(propertyName, requiredType);
		return desc(property);
	}

	public <T extends Comparable<T>> BeanComparator<E> desc(Getter<E, T> getter) {
		comparatorChains.add(new ComparatorChain(getter).reverse());
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
