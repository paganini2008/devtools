package com.github.paganini2008.devtools.beans.oq;

/**
 * 
 * Selectable
 * 
 * @author Fred Feng
 *
 */
public interface Selectable<E> extends Listable<E>, Countable<E>, Aggregation<E> {

	Selectable<E> filter(Expression<E> expression);

	Sortable<E> sort();

	Groupable<E> group();

}
