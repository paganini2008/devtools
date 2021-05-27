package com.github.paganini2008.devtools.collection;

/**
 * 
 * BoundedMap
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface BoundedMap<K, V> {

	int getMaxSize();

	default void onEviction(K eldestKey, V eldestValue) {
	}

}
