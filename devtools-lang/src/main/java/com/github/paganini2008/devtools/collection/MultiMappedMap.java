/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

/**
 * 
 * MultiMappedMap
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MultiMappedMap<K, N, V> extends AbstractMap<K, Map<N, V>> implements Map<K, Map<N, V>>, Serializable {

	private static final long serialVersionUID = 1256299819433654455L;

	private final Map<K, Map<N, V>> delegate;
	private final Supplier<Map<N, V>> supplier;

	public MultiMappedMap() {
		this(new ConcurrentHashMap<K, Map<N, V>>(), () -> new ConcurrentHashMap<N, V>());
	}

	public MultiMappedMap(Comparator<N> c) {
		this(new ConcurrentHashMap<K, Map<N, V>>(), () -> new ConcurrentSkipListMap<N, V>(c));
	}

	public MultiMappedMap(Supplier<Map<N, V>> supplier) {
		this(new ConcurrentHashMap<K, Map<N, V>>(), supplier);
	}

	public MultiMappedMap(Map<K, Map<N, V>> delegate) {
		this(delegate, () -> new ConcurrentHashMap<N, V>());
	}

	public MultiMappedMap(Map<K, Map<N, V>> delegate, Supplier<Map<N, V>> supplier) {
		this.delegate = delegate;
		this.supplier = supplier;
	}

	public V get(K key, N name) {
		return get(key, name, (V) null);
	}

	public V get(K key, N name, V defaultValue) {
		return get(key, name, () -> defaultValue);
	}

	public V get(K key, N name, Supplier<V> valueSupplier) {
		Map<N, V> map = delegate.get(key);
		V v = null;
		if (map != null) {
			v = map.getOrDefault(name, valueSupplier.get());
		}
		return v;
	}

	public V getIfNecessary(K key, N name, V defaultValue) {
		return getIfNecessary(key, name, () -> defaultValue);
	}

	public V getIfNecessary(K key, N name, Supplier<V> valueSupplier) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, supplier.get());
			map = delegate.get(key);
		}
		V v = map.get(name);
		if (v == null) {
			map.put(name, valueSupplier.get());
			v = map.get(name);
		}
		return v;
	}

	public Map<N, V> get(Object key) {
		return delegate.get(key);
	}

	public Map<N, V> remove(Object key) {
		return delegate.remove(key);
	}

	public V removeValue(K key, N name) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			return map.remove(name);
		}
		return null;
	}

	public void clear() {
		delegate.clear();
	}

	public void clear(K key) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			map.clear();
		}
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public boolean containsValue(K key, N name) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			return map.containsKey(name);
		}
		return false;
	}

	public void append(K key, Map<N, V> value) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, supplier.get());
			map = delegate.get(key);
		}
		map.putAll(value);
	}

	public Map<N, V> put(K key, Map<N, V> value) {
		return delegate.put(key, value);
	}

	public V put(K key, N name, V value) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, supplier.get());
			map = delegate.get(key);
		}
		return map.put(name, value);
	}

	public V putIfAbsent(K key, N name, V value) {
		return putIfAbsent(key, name, () -> value);
	}

	public V putIfAbsent(K key, N name, Supplier<V> valueSupplier) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, supplier.get());
			map = delegate.get(key);
		}
		return map.putIfAbsent(name, valueSupplier.get());
	}

	public int size() {
		return delegate.size();
	}

	public int size(K key) {
		Map<N, V> map = delegate.get(key);
		return map != null ? map.size() : 0;
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public Set<Map.Entry<K, Map<N, V>>> entrySet() {
		return delegate.entrySet();
	}

	public String toString() {
		return delegate.toString();
	}
}
