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
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class MultiSetMap<K, V> extends AbstractMap<K, NavigableSet<V>> implements Map<K, NavigableSet<V>>, Serializable {

	private static final long serialVersionUID = 83462015304015642L;

	private final Map<K, NavigableSet<V>> delegate;
	private final Supplier<NavigableSet<V>> supplier;

	public MultiSetMap() {
		this((left, right) -> 0);
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

	public void clear(K key) {
		Set<V> set = delegate.get(key);
		if (set != null) {
			set.clear();
		}
	}

	public NavigableSet<V> remove(Object key) {
		return delegate.remove(key);
	}

	public V pollLast(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set.pollLast();
	}

	public V pollFirst(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set.pollFirst();
	}

	public V peekFirst(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set.first();
	}

	public V peekLast(K key) {
		NavigableSet<V> set = delegate.get(key);
		return set.last();
	}

	public NavigableSet<V> get(Object key) {
		return delegate.get(key);
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

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

	public boolean containsValue(K key, V value) {
		NavigableSet<V> q = delegate.get(key);
		return q != null ? q.contains(value) : false;
	}

	public int size() {
		return delegate.size();
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public NavigableSet<V> put(K key, NavigableSet<V> value) {
		return delegate.put(key, value);
	}

	public void addAll(K key, Collection<V> values) {
		NavigableSet<V> set = delegate.get(key);
		if (set == null) {
			delegate.putIfAbsent(key, supplier.get());
			set = delegate.get(key);
		}
		set.addAll(values);
	}

	public V add(K key, V value) {
		NavigableSet<V> set = delegate.get(key);
		if (set == null) {
			delegate.putIfAbsent(key, supplier.get());
			set = delegate.get(key);
		}
		set.add(value);
		return value;
	}

	public Map<K, V> toSingleValueMap() {
		final Map<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, NavigableSet<V>> entry : delegate.entrySet()) {
			map.put(entry.getKey(), entry.getValue().first());
		}
		return map;
	}

	public String toString() {
		return delegate.toString();
	}

}
