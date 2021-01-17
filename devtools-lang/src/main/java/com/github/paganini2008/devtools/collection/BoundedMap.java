package com.github.paganini2008.devtools.collection;

/**
 * 
 * BoundedMap
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface BoundedMap<K, V> {

	default void onEviction(K eldestKey, V eldestValue) {
	}

}
