package com.github.paganini2008.devtools.collection;

/**
 * 
 * EvictionListener
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface EvictionListener<K, V> {

	void onEviction(K key, V value);

}
