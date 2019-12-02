package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * KeyConversionMap
 * 
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-07
 * @version 1.0
 */
public abstract class KeyConversionMap<T, K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<K, V> delegate;
	private final Map<T, K> keys = new HashMap<T, K>();

	protected KeyConversionMap(Map<K, V> delegate) {
		if (delegate == null) {
			delegate = new HashMap<K, V>();
		}
		this.delegate = delegate;
		for (K key : delegate.keySet()) {
			keys.put(convertKey(key), key);
		}
	}

	public boolean containsKey(Object key) {
		Object realKey = keys.get(convertKey(key));
		return delegate.containsKey(realKey);
	}

	public V get(Object key) {
		Object realKey = keys.get(convertKey(key));
		return delegate.get(realKey);
	}

	public V put(K key, V value) {
		keys.put(convertKey(key), key);
		return delegate.put(key, value);
	}

	public V remove(Object key) {
		Object realKey = keys.remove(convertKey(key));
		return delegate.remove(realKey);
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	protected abstract T convertKey(Object key);
}
