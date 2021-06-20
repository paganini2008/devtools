/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

/**
 * 
 * MultiDequeMap
 *
 * @author Fred Feng
 * @since 1.0
 */
public class MultiDequeMap<K, V> extends AbstractMap<K, Deque<V>> implements Map<K, Deque<V>>, Serializable {

	private static final long serialVersionUID = -7112963888352476892L;
	
	private final Map<K, Deque<V>> delegate;
	private final Supplier<Deque<V>> supplier;

	public MultiDequeMap() {
		this(new ConcurrentHashMap<K, Deque<V>>(), () -> {
			return new ConcurrentLinkedDeque<V>();
		});
	}

	public MultiDequeMap(Map<K, Deque<V>> delegate, Supplier<Deque<V>> supplier) {
		this.delegate = delegate;
		this.supplier = supplier;
	}

	public void clear() {
		delegate.clear();
	}

	public void clear(K key) {
		Deque<V> q = delegate.get(key);
		if (q != null) {
			q.clear();
		}
	}

	public Deque<V> remove(Object key) {
		return delegate.remove(key);
	}

	public V pollLast(K key) {
		Deque<V> q = delegate.get(key);
		return q.pollLast();
	}

	public V pollFirst(K key) {
		Deque<V> q = delegate.get(key);
		return q.pollFirst();
	}

	public V peekFirst(K key) {
		Deque<V> q = delegate.get(key);
		return q.peekFirst();
	}

	public V peekLast(K key) {
		Deque<V> q = delegate.get(key);
		return q.peekLast();
	}

	public Deque<V> get(Object key) {
		return delegate.get(key);
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public void removeValue(K key, V value) {
		Deque<V> q = delegate.get(key);
		if (q != null) {
			while (q.contains(value)) {
				q.remove(value);
			}
		}
	}

	public Set<Map.Entry<K, Deque<V>>> entrySet() {
		return delegate.entrySet();
	}

	public boolean containsValue(K key, V value) {
		Deque<V> q = delegate.get(key);
		return q != null ? q.contains(value) : false;
	}

	public int size() {
		return delegate.size();
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public Deque<V> put(K key, Deque<V> value) {
		return delegate.put(key, value);
	}

	public void addAll(K key, Collection<V> values) {
		Deque<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, supplier.get());
			list = delegate.get(key);
		}
		list.addAll(values);
	}

	public V addFirst(K key, V value) {
		Deque<V> q = delegate.get(key);
		if (q == null) {
			delegate.putIfAbsent(key, supplier.get());
			q = delegate.get(key);
		}
		q.addFirst(value);
		return value;
	}

	public V addLast(K key, V value) {
		Deque<V> q = delegate.get(key);
		if (q == null) {
			delegate.putIfAbsent(key, supplier.get());
			q = delegate.get(key);
		}
		q.addLast(value);
		return value;
	}

	public Map<K, V> toSingleValueMap() {
		final Map<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, Deque<V>> entry : delegate.entrySet()) {
			map.put(entry.getKey(), entry.getValue().getFirst());
		}
		return map;
	}

	public String toString() {
		return delegate.toString();
	}

}
