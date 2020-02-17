package com.github.paganini2008.devtools.beans.streaming;

import java.util.function.Function;

/**
 * 
 * Orders
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public abstract class Orders {

	public static <E, T extends Comparable<T>> Sort<E> descending(final String attributeName, final Class<T> requiredType) {
		return descending(Property.forName(attributeName, requiredType));
	}

	public static <E, T extends Comparable<T>> Sort<E> ascending(final String attributeName, final Class<T> requiredType) {
		return ascending(Property.forName(attributeName, requiredType));
	}

	public static <E, T extends Comparable<T>> Sort<E> descending(final Function<E, T> function) {
		BeanSort<E> sort = new BeanSort<E>();
		sort.descending(function);
		return sort;
	}

	public static <E, T extends Comparable<T>> Sort<E> ascending(final Function<E, T> function) {
		BeanSort<E> sort = new BeanSort<E>();
		sort.ascending(function);
		return sort;
	}

	public static <E, T extends Comparable<T>> Sort<Group<E>> groupAscending(final String attributeName, final Class<T> requiredType) {
		return groupAscending(group -> {
			return MappedBy.forName(attributeName, requiredType).apply(group.getAttributes());
		});
	}

	public static <E, T extends Comparable<T>> Sort<Group<E>> groupDescending(final String attributeName, final Class<T> requiredType) {
		return groupDescending(group -> {
			return MappedBy.forName(attributeName, requiredType).apply(group.getAttributes());
		});
	}

	public static <E, T extends Comparable<T>> Sort<Group<E>> groupDescending(final GroupFunction<E, T> function) {
		BeanSort<Group<E>> sort = new BeanSort<Group<E>>();
		sort.descending(function);
		return sort;
	}

	public static <E, T extends Comparable<T>> Sort<Group<E>> groupAscending(final GroupFunction<E, T> function) {
		BeanSort<Group<E>> sort = new BeanSort<Group<E>>();
		sort.ascending(function);
		return sort;
	}
}
