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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Supplier;

/**
 * 
 * MultiSetMap
 * <p>
 * A map (thread-safe by default) holding a sorted set of values per key; sets are created on
 * demand.
 *
 * @author Fred Feng
 * @date 2020-06-19
 * @since 2.0.1
 */
public class MultiSetMap<K, V> extends AbstractMap<K, NavigableSet<V>> implements Map<K, NavigableSet<V>>, Serializable {

	private static final long serialVersionUID = 83462015304015642L;

	private final Map<K, NavigableSet<V>> delegate;
	private final Supplier<NavigableSet<V>> supplier;

	public MultiSetMap() {
		this(new ConcurrentHashMap<K, NavigableSet<V>>(), () -> {
			return new ConcurrentSkipListSet<V>();
		});
	}

	public MultiSetMap(Comparator<V> c) {
		this(new ConcurrentHashMap<K, NavigableSet<V>>(), () -> {
			return new ConcurrentSkipListSet<V>(c);
		});
	}

	public MultiSetMap(Supplier<NavigableSet<V>> supplier) {
		this(new ConcurrentHashMap<K, NavigableSet<V>>(), supplier);
	}

	public MultiSetMap(Map<K, NavigableSet<V>> delegate, Supplier<NavigableSet<V>> supplier) {
		this.delegate = delegate;
		this.supplier = supplier;
	}

	public void clear() {
		delegate.clear();
	}

	/**
	 * Removes all values of the key, keeping the key.
	 */
	public void clear(K key) {
		Set<V> set = delegate.get(key);
		if (set != null) {
			set.clear();
		}
	}

	public NavigableSet<V> remove(Object key) {
		return delegate.remove(key);
	}

	/**
	 * Removes and returns the largest value of the key, or null if none.
	 */
	public V pollLast(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set != null ? set.pollLast() : null;
	}

	/**
	 * Removes and returns the smallest value of the key, or null if none.
	 */
	public V pollFirst(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set != null ? set.pollFirst() : null;
	}

	/**
	 * Returns the smallest value of the key, or null if none.
	 */
	public V peekFirst(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set != null && !set.isEmpty() ? set.first() : null;
	}

	/**
	 * Returns the largest value of the key, or null if none.
	 */
	public V peekLast(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set != null && !set.isEmpty() ? set.last() : null;
	}

	public NavigableSet<V> get(Object key) {
		return delegate.get(key);
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	/**
	 * Removes the value from the key.
	 */
	public void removeValue(K key, V value) {
		NavigableSet<V> set = delegate.get(key);
		if (set != null) {
			while (set.contains(value)) {
				set.remove(value);
			}
		}
	}

	public Set<Map.Entry<K, NavigableSet<V>>> entrySet() {
		return delegate.entrySet();
	}

	/**
	 * Checks if the key holds the value.
	 */
	public boolean containsValue(K key, V value) {
		NavigableSet<V> q = delegate.get(key);
		return q != null ? q.contains(value) : false;
	}

	public int size() {
		return delegate.size();
	}

	/**
	 * Returns the number of values of the key (0 if absent).
	 */
	public int size(K key) {
		NavigableSet<V> q = delegate.get(key);
		return q != null ? q.size() : 0;
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public NavigableSet<V> put(K key, NavigableSet<V> value) {
		return delegate.put(key, value);
	}

	/**
	 * Adds the values to the key.
	 */
	public void addAll(K key, Collection<V> values) {
		NavigableSet<V> set = delegate.get(key);
		if (set == null) {
			delegate.putIfAbsent(key, supplier.get());
			set = delegate.get(key);
		}
		set.addAll(values);
	}

	/**
	 * Adds the value to the key.
	 *
	 * @return the given value
	 */
	public V add(K key, V value) {
		NavigableSet<V> set = delegate.get(key);
		if (set == null) {
			delegate.putIfAbsent(key, supplier.get());
			set = delegate.get(key);
		}
		set.add(value);
		return value;
	}

	/**
	 * Returns a map holding the smallest value of every key (null for an empty set).
	 */
	public Map<K, V> toSingleValueMap() {
		final Map<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, NavigableSet<V>> entry : delegate.entrySet()) {
			NavigableSet<V> set = entry.getValue();
			map.put(entry.getKey(), set.isEmpty() ? null : set.first());
		}
		return map;
	}

	public String toString() {
		return delegate.toString();
	}

}
