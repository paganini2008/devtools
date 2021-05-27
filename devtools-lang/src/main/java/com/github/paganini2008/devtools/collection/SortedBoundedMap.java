package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * SortedBoundedMap
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class SortedBoundedMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, BoundedMap<K, V> {

	private static final long serialVersionUID = -2786447946165755013L;

	private final NavigableSet<K> keys;
	private final int maxSize;
	private final Map<K, V> delegate;

	public SortedBoundedMap(int maxSize) {
		this(new LinkedHashMap<K, V>(), maxSize);
	}

	public SortedBoundedMap(Map<K, V> delegate, int maxSize) {
		this(delegate, maxSize, new TreeSet<K>());
	}

	protected SortedBoundedMap(Map<K, V> delegate, int maxSize, NavigableSet<K> keys) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = keys;
	}

	private boolean asc = true;

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public V get(Object key) {
		return delegate.get(key);
	}

	@Override
	public V put(K key, V value) {
		V eldestValue = delegate.put(key, value);
		ensureCapacity(key);
		return eldestValue;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V eldestValue = delegate.putIfAbsent(key, value);
		ensureCapacity(key);
		return eldestValue;
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(key);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public Collection<V> values() {
		return delegate.values();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	private void ensureCapacity(K key) {
		boolean reached;
		K eldestKey = null;
		V eldestValue = null;
		synchronized (keys) {
			if (!keys.contains(key)) {
				keys.add(key);
			}
			if (reached = (keys.size() > maxSize)) {
				eldestKey = asc ? keys.pollFirst() : keys.pollLast();
				eldestValue = delegate.remove(eldestKey);
			}
		}
		if (reached) {
			onEviction(eldestKey, eldestValue);
		}
	}

}
