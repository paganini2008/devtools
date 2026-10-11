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
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

/**
 * 
 * MultiMappedMap
 * <p>
 * A two-level map (key -&gt; name -&gt; value), thread-safe by default; inner maps are created
 * on demand.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class MultiMappedMap<K, N, V> extends AbstractMap<K, Map<N, V>> implements Map<K, Map<N, V>>, Serializable {

	private static final long serialVersionUID = 1256299819433654455L;

	private final Map<K, Map<N, V>> delegate;
	private final Supplier<Map<N, V>> supplier;

	public MultiMappedMap() {
		this(new ConcurrentHashMap<K, Map<N, V>>(), (Supplier<Map<N, V>> & Serializable) () -> new ConcurrentHashMap<N, V>());
	}

	public MultiMappedMap(Comparator<N> c) {
		this(new ConcurrentHashMap<K, Map<N, V>>(), (Supplier<Map<N, V>> & Serializable) () -> new ConcurrentSkipListMap<N, V>(c));
	}

	public MultiMappedMap(Supplier<Map<N, V>> supplier) {
		this(new ConcurrentHashMap<K, Map<N, V>>(), supplier);
	}

	public MultiMappedMap(Map<K, Map<N, V>> delegate) {
		this(delegate, (Supplier<Map<N, V>> & Serializable) () -> new ConcurrentHashMap<N, V>());
	}

	public MultiMappedMap(Map<K, Map<N, V>> delegate, Supplier<Map<N, V>> supplier) {
		this.delegate = delegate;
		this.supplier = supplier;
	}

	/**
	 * Returns the value of the name under the key, or null if absent.
	 */
	public V get(K key, N name) {
		return get(key, name, (V) null);
	}

	/**
	 * Returns the value of the name under the key, or the default value if absent.
	 */
	public V get(K key, N name, V defaultValue) {
		return get(key, name, () -> defaultValue);
	}

	/**
	 * Returns the value of the name under the key, or the supplied value if absent (nothing is stored).
	 */
	public V get(K key, N name, Supplier<V> valueSupplier) {
		Map<N, V> map = delegate.getOrDefault(key, MapUtils.emptyMap());
		return map.getOrDefault(name, valueSupplier.get());
	}

	/**
	 * Returns the value of the name under the key, storing the default value first if absent.
	 */
	public V getIfNecessary(K key, N name, V defaultValue) {
		return getIfNecessary(key, name, () -> defaultValue);
	}

	/**
	 * Returns the value of the name under the key, storing the supplied value first if absent (a null supplied value is not stored).
	 */
	public V getIfNecessary(K key, N name, Supplier<V> valueSupplier) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, supplier.get());
			map = delegate.get(key);
		}
		V v = map.get(name);
		if (v == null) {
			V newValue = valueSupplier.get();
			if (newValue != null) {
				map.putIfAbsent(name, newValue);
				v = map.get(name);
			}
		}
		return v;
	}

	public Map<N, V> get(Object key) {
		return delegate.get(key);
	}

	public Map<N, V> remove(Object key) {
		return delegate.remove(key);
	}

	/**
	 * Removes the name under the key, returning its value or null.
	 */
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

	/**
	 * Removes all names under the key, keeping the key.
	 */
	public void clear(K key) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			map.clear();
		}
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	/**
	 * Checks if the name exists under the key.
	 */
	public boolean containsValue(K key, N name) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			return map.containsKey(name);
		}
		return false;
	}

	/**
	 * Puts all entries of the map under the key.
	 */
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

	/**
	 * Sets the value of the name under the key, returning the previous value.
	 */
	public V put(K key, N name, V value) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, supplier.get());
			map = delegate.get(key);
		}
		return map.put(name, value);
	}

	/**
	 * Sets the value of the name under the key if absent, returning the existing value or null.
	 */
	public V putIfAbsent(K key, N name, V value) {
		return putIfAbsent(key, name, () -> value);
	}

	/**
	 * Sets the supplied value of the name under the key if absent, returning the existing value or null.
	 */
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

	/**
	 * Returns the number of names under the key (0 if absent).
	 */
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
