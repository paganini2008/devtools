package com.github.paganini2008.devtools.comparator.map;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.comparator.AbstractComparator;
import com.github.paganini2008.devtools.comparator.ReverseComparator;

/**
 * MapComparator
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class MapComparator<K, V> extends AbstractComparator<Map<K, V>> {

	public MapComparator() {
	}

	static class ClassifiedComparator<K, V> implements Comparator<Map<K, V>> {

		ClassifiedComparator(K name) {
			this.name = name;
		}

		final K name;

		public int compare(Map<K, V> a, Map<K, V> b) {
			Object leftObject = a.get(name);
			Object rightObject = b.get(name);
			int leftValue = leftObject instanceof Comparable ? 1 : 0;
			int rightValue = rightObject instanceof Comparable ? 1 : 0;
			return leftValue * rightValue == 1 ? leftObject.getClass() == rightObject.getClass() ? 1 : 0 : 0;
		}
	}

	static class ComparatorChain<K, V> extends ClassifiedComparator<K, V> {

		ComparatorChain(K name) {
			super(name);
		}

		public int compare(Map<K, V> a, Map<K, V> b) {
			int result = super.compare(a, b);
			if (result == 1) {
				V leftObject = a.get(name);
				V rightObject = b.get(name);
				return ((Comparable) leftObject).compareTo((Comparable) rightObject);
			}
			return 0;
		}

		public String toString() {
			return "[ComparatorChain] Order by: " + name;
		}

	}

	private final List<Comparator<Map<K, V>>> comparatorChains = new ArrayList<Comparator<Map<K, V>>>();

	public void reset() {
		comparatorChains.clear();
	}

	public MapComparator<K, V> asc(K name) {
		comparatorChains.add(new ComparatorChain(name));
		return this;
	}

	public MapComparator<K, V> desc(K name) {
		comparatorChains.add(new ReverseComparator<Map<K, V>>(new ComparatorChain(name)));
		return this;
	}

	public int compare(Map<K, V> a, Map<K, V> b) {
		int value = 0;
		for (Comparator<Map<K, V>> chain : comparatorChains) {
			if ((value = chain.compare(a, b)) != 0) {
				break;
			}
		}
		return value;
	}

}
