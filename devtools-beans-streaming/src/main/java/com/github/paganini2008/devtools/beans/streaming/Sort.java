package com.github.paganini2008.devtools.beans.streaming;

import java.util.Comparator;
import java.util.function.Function;

/**
 * 
 * Sort
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Sort<E> extends Comparator<E> {

	<T extends Comparable<T>> Sort<E> ascending(Function<E, T> function);

	<T extends Comparable<T>> Sort<E> descending(Function<E, T> function);
}
