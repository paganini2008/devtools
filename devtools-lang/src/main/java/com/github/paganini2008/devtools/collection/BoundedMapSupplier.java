package com.github.paganini2008.devtools.collection;

import java.util.Map;

/**
 * 
 * BoundedMapSupplier
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface BoundedMapSupplier<K, V> {

	Map<K, V> get(int maxSize, EvictionListener<K, V> evictionListener);

}
