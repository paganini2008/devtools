package com.github.paganini2008.devtools.beans.streaming;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 
 * Sorter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Sorter<E> extends Comparator<E> {

	<T extends Comparable<T>> Sorter<E> ascending(Function<E, T> function);

	<T extends Comparable<T>> Sorter<E> descending(Function<E, T> function);
}
