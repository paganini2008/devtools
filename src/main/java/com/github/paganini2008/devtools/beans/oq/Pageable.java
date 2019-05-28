package com.github.paganini2008.devtools.beans.oq;

/**
 * 
 * Pageable
 * 
 * @author Fred Feng
 *
 */
public interface Pageable<E> extends Listable<E> {

	public Pageable<E> offset(int offset);

	public Pageable<E> limit(int limit);

}
