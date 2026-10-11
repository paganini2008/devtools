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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * 
 * MultiListMap
 * <p>
 * A map (thread-safe by default) holding a list of values per key; lists are created on
 * demand.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class MultiListMap<K, V> extends AbstractMap<K, List<V>> implements Map<K, List<V>>, Serializable {

	private static final long serialVersionUID = 4293668328277273376L;
	private final Map<K, List<V>> delegate;
	private final Supplier<List<V>> supplier;

	public MultiListMap() {
		this(() -> {
			return new CopyOnWriteArrayList<V>();
		});
	}

	public MultiListMap(Supplier<List<V>> supplier) {
		this(new ConcurrentHashMap<K, List<V>>(), supplier);
	}

	public MultiListMap(Map<K, List<V>> delegate, Supplier<List<V>> supplier) {
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
		List<V> list = delegate.get(key);
		if (list != null) {
			list.clear();
		}
	}

	public List<V> remove(Object key) {
		return delegate.remove(key);
	}

	/**
	 * Removes the value at the index (negative counts from the end), returning null if absent.
	 */
	public V remove(K key, int index) {
		List<V> list = delegate.get(key);
		return ListUtils.remove(list, index);
	}

	/**
	 * Removes and returns the last value of the key, or null if none.
	 */
	public V pollLast(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.removeLast(list);
	}

	/**
	 * Removes and returns the first value of the key, or null if none.
	 */
	public V pollFirst(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.removeFirst(list);
	}

	/**
	 * Returns the first value of the key, or null if none.
	 */
	public V peekFirst(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.getFirst(list);
	}

	/**
	 * Returns the last value of the key, or null if none.
	 */
	public V peekLast(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.getLast(list);
	}

	/**
	 * Returns the value at the index (negative counts from the end), or null if absent.
	 */
	public V get(K key, int index) {
		List<V> list = delegate.get(key);
		return ListUtils.get(list, index);
	}

	public List<V> get(Object key) {
		return delegate.get(key);
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	/**
	 * Removes all occurrences of the value from the key.
	 */
	public void removeValue(K key, V value) {
		List<V> list = delegate.get(key);
		if (list != null) {
			while (list.contains(value)) {
				list.remove(value);
			}
		}
	}

	public Set<Map.Entry<K, List<V>>> entrySet() {
		return delegate.entrySet();
	}

	/**
	 * Checks if the key holds the value.
	 */
	public boolean containsValue(K key, V value) {
		List<V> list = delegate.get(key);
		return list != null ? list.contains(value) : false;
	}

	public int size() {
		return delegate.size();
	}

	/**
	 * Returns the number of values of the key (0 if absent).
	 */
	public int size(String key) {
		List<V> list = delegate.get(key);
		return list != null ? list.size() : 0;
	}

	/**
	 * Returns the keys.
	 */
	public Set<K> keys() {
		return delegate.keySet();
	}

	public List<V> put(K key, List<V> value) {
		return delegate.put(key, value);
	}

	/**
	 * Appends the values to the key.
	 */
	public void addAll(K key, Collection<V> values) {
		List<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, supplier.get());
			list = delegate.get(key);
		}
		list.addAll(values);
	}

	/**
	 * Appends the value to the key.
	 *
	 * @return the given value
	 */
	public V add(K key, V value) {
		List<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, supplier.get());
			list = delegate.get(key);
		}
		list.add(value);
		return value;
	}

	/**
	 * Returns a map holding the first value of every key (null for an empty list).
	 */
	public Map<K, V> toSingleValueMap() {
		final Map<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, List<V>> entry : delegate.entrySet()) {
			map.put(entry.getKey(), ListUtils.getFirst(entry.getValue()));
		}
		return map;
	}

	public String toString() {
		return delegate.toString();
	}

}
