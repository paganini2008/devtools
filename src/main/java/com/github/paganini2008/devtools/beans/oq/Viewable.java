package com.github.paganini2008.devtools.beans.oq;

import java.math.RoundingMode;

import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * Viewable
 * 
 * @author Fred Feng
 *
 */
public interface Viewable<E, V> extends Listable<V> {

	Sortable<V> sort();

	Viewable<E, V> count(String alias);

	<T extends Comparable<T>> Viewable<E, V> max(Getter<E, T> getter, String alias);

	<T extends Comparable<T>> Viewable<E, V> min(Getter<E, T> getter, String alias);

	<T extends Number> Viewable<E, V> sum(Getter<E, T> getter, String alias);

	<T extends Number> Viewable<E, V> avg(Getter<E, T> getter, String alias, int scale, RoundingMode roundingMode);

}