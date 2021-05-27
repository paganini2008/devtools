package com.github.paganini2008.devtools.collection;

/**
 * 
 * BoundedCollection
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface BoundedCollection<E> {

	default void onEviction(E element) {
	}

}
