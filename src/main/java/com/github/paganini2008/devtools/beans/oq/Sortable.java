package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * Sortable
 * 
 * @author Fred Feng
 *
 */
public interface Sortable<E> extends Listable<E> {

	<T extends Comparable<T>> Sortable<E> asc(Getter<E, T> getter);

	<T extends Comparable<T>> Sortable<E> desc(Getter<E, T> getter);

	Pageable<E> page();

}
