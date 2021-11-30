package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * MutableMap
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public abstract class MutableMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<K, V> delegate;

	protected MutableMap(Map<K, V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public V get(Object key) {
		return delegate.get(mutate(key));
	}

	@Override
	public V put(K key, V value) {
		return delegate.put(mutate(key), value);
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(mutate(key));
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(mutate(key));
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	protected abstract K mutate(Object key);

}
