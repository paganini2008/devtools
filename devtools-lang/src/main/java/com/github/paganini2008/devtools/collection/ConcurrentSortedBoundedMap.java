/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BiFunction;

/**
 * 
 * ConcurrentSortedBoundedMap
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ConcurrentSortedBoundedMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, BoundedMap<K, V> {

	private static final long serialVersionUID = -2786447946165755013L;

	private final NavigableSet<K> keys;
	private final int maxSize;
	private final Map<K, V> delegate;

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
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return delegate.merge(key, value, remappingFunction);
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
	public Map<K, V> getDelegate() {
		return delegate;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	private boolean ensureCapacity(K key) {
		boolean reached = false;
		K eldestKey = null;
		V eldestValue = null;
		synchronized (keys) {
			keys.add(key);
			if (reached = (keys.size() > maxSize)) {
				eldestKey = asc ? keys.pollFirst() : keys.pollLast();
				eldestValue = delegate.remove(eldestKey);
			}
		}
		if (reached) {
			onEviction(eldestKey, eldestValue);
		}
		return reached;
	}

}
