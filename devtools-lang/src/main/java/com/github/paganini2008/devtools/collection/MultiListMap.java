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
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class MultiListMap<K, V> extends AbstractMap<K, List<V>> implements Map<K, List<V>>, Serializable {

	private static final long serialVersionUID = 4293668328277273376L;
	private final Map<K, List<V>> delegate;
	private final Supplier<List<V>> supplier;

	public MultiListMap() {
		this(new ConcurrentHashMap<K, List<V>>(), () -> {
			return new CopyOnWriteArrayList<V>();
		});
	}

	public MultiListMap(Map<K, List<V>> delegate, Supplier<List<V>> supplier) {
		this.delegate = delegate;
		this.supplier = supplier;
	}

	public void clear() {
		delegate.clear();
	}

	public void clear(K key) {
		List<V> list = delegate.get(key);
		if (list != null) {
			list.clear();
		}
	}

	public List<V> remove(Object key) {
		return delegate.remove(key);
	}

	public V remove(K key, int index) {
		List<V> list = delegate.get(key);
		return ListUtils.remove(list, index);
	}

	public V pollLast(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.removeLast(list);
	}

	public V pollFirst(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.removeFirst(list);
	}

	public V peekFirst(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.getFirst(list);
	}

	public V peekLast(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.getLast(list);
	}

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

	public boolean containsValue(K key, V value) {
		List<V> list = delegate.get(key);
		return list != null ? list.contains(value) : false;
	}

	public int size() {
		return delegate.size();
	}

	public Set<K> keys() {
		return delegate.keySet();
	}

	public List<V> put(K key, List<V> value) {
		return delegate.put(key, value);
	}

	public void addAll(K key, Collection<V> values) {
		List<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, supplier.get());
			list = delegate.get(key);
		}
		list.addAll(values);
	}

	public V add(K key, V value) {
		List<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, supplier.get());
			list = delegate.get(key);
		}
		list.add(value);
		return value;
	}

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
