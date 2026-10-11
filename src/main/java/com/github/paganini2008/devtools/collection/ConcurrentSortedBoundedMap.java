/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 
 * ConcurrentSortedBoundedMap
 * <p>
 * A thread-safe map bounded by a maximum size which, when the size is
 * exceeded, evicts the entry with the smallest key (or the largest key if
 * {@link #setAsc(boolean)} is set to false). Keys must be mutually comparable.
 * 
 * @author Fred Feng
 * @date 2021-02-18
 *
 * @since 2.0.1
 */
public class ConcurrentSortedBoundedMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, BoundedMap<K, V> {

	private static final long serialVersionUID = -2786447946165755013L;

	private final NavigableSet<K> keys;
	private final int maxSize;
	private final Map<K, V> delegate;

	/**
	 * Creates a ConcurrentSortedBoundedMap backed by a
	 * {@link ConcurrentHashMap}.
	 * 
	 * @param maxSize
	 *            the maximum number of entries
	 */
	public ConcurrentSortedBoundedMap(int maxSize) {
		this(new ConcurrentHashMap<K, V>(), maxSize);
	}

	public ConcurrentSortedBoundedMap(Map<K, V> delegate, int maxSize) {
		this(delegate, maxSize, new ConcurrentSkipListSet<K>());
	}

	protected ConcurrentSortedBoundedMap(Map<K, V> delegate, int maxSize, NavigableSet<K> keys) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = keys;
	}

	private volatile boolean asc = true;

	/**
	 * Sets the eviction order.
	 * 
	 * @param asc
	 *            true (default) to evict the smallest key first, false to
	 *            evict the largest key first
	 */
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
		V previous;
		Map.Entry<K, V> eldest;
		synchronized (keys) {
			previous = delegate.put(key, value);
			eldest = ensureCapacity(key);
		}
		if (eldest != null) {
			onEviction(eldest.getKey(), eldest.getValue());
		}
		return previous;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V previous;
		Map.Entry<K, V> eldest;
		synchronized (keys) {
			previous = delegate.putIfAbsent(key, value);
			eldest = ensureCapacity(key);
		}
		if (eldest != null) {
			onEviction(eldest.getKey(), eldest.getValue());
		}
		return previous;
	}

	@Override
	public V remove(Object key) {
		synchronized (keys) {
			keys.remove(key);
			return delegate.remove(key);
		}
	}

	@Override
	public void clear() {
		synchronized (keys) {
			keys.clear();
			delegate.clear();
		}
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
	public Map<K, V> getDelegate() {
		return delegate;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySetView<K, V>(delegate, this::remove);
	}

	private Map.Entry<K, V> ensureCapacity(K key) {
		keys.add(key);
		if (keys.size() > maxSize) {
			K eldestKey = asc ? keys.pollFirst() : keys.pollLast();
			V eldestValue = delegate.remove(eldestKey);
			return new SimpleImmutableEntry<K, V>(eldestKey, eldestValue);
		}
		return null;
	}

}
