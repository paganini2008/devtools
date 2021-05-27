package com.github.paganini2008.devtools.beans.streaming;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 
 * Sorter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Sorter<E> extends Comparator<E> {

	default <T extends Comparable<T>> Sorter<E> ascending(String attributeName, Class<T> requiredType) {
		return ascending(Property.forName(attributeName, requiredType));
	}

	<T extends Comparable<T>> Sorter<E> ascending(Function<E, T> function);

	default <T extends Comparable<T>> Sorter<E> descending(String attributeName, Class<T> requiredType) {
		return descending(Property.forName(attributeName, requiredType));
	}

	<T extends Comparable<T>> Sorter<E> descending(Function<E, T> function);
}
