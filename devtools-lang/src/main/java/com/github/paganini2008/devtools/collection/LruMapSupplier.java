package com.github.paganini2008.devtools.collection;

import java.util.Map;

/**
 * 
 * LruMapSupplier
 *
 * @author Fred Feng
 * @version 1.0
 */
@FunctionalInterface
public interface LruMapSupplier<K, V> {

	Map<K, V> get(int maxSize, EvictionListener<K, V> evictionListener);

}
