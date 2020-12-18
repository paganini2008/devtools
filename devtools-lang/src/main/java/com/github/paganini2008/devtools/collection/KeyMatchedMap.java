package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * KeyMatchedMap
 *
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public abstract class KeyMatchedMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;
	private final Map<K, V> delegate;
	private final Set<K> keys = Collections.synchronizedNavigableSet(new TreeSet<K>());

	protected KeyMatchedMap(Map<K, V> delegate) {
		this.delegate = delegate;
	}

	public boolean containsKey(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.containsKey(key) : false;
	}

	public V get(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.get(key) : null;
	}

	public V put(K key, V value) {
		Assert.isNull(key, "Non null key");
		keys.add(key);
		return delegate.put(key, value);
	}

	public V remove(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.remove(key) : null;
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	private K matches(Object inputKey) {
		for (K key : keys) {
			if (apply(key, inputKey)) {
				return key;
			}
		}
		return null;
	}

	protected abstract boolean apply(K key, Object inputKey);
}
