package com.github.paganini2008.devtools.collection;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * LruBoundedMapSupplier
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class LruBoundedMapSupplier<K, V> implements BoundedMapSupplier<K, V> {

	@Override
	public Map<K, V> get(final int maxSize, final EvictionListener<K, V> evictionListener) {
		if (maxSize < 1) {
			throw new IllegalArgumentException("MaxSize must greater then zero");
		}
		return Collections.synchronizedMap(new LinkedHashMap<K, V>(16, 0.75F, true) {

			private static final long serialVersionUID = -4000636962958369285L;

			protected boolean removeEldestEntry(Map.Entry<K, V> eldestEntry) {
				boolean result;
				if (result = size() > maxSize) {
					if (evictionListener != null) {
						evictionListener.onEviction(eldestEntry.getKey(), eldestEntry.getValue());
					}
				}
				return result;
			}
		});
	}

}
