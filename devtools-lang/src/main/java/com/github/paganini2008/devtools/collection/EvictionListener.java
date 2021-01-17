package com.github.paganini2008.devtools.collection;

/**
 * 
 * EvictionListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface EvictionListener<K, V> {

	void onEviction(K key, V value);

}
