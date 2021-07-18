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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 
 * SimpleBoundedMap
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class SimpleBoundedMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, BoundedMap<K, V> {

	private static final long serialVersionUID = -4746340195536492722L;

	private final Queue<K> keys;
	private final int maxSize;
	private final Map<K, V> delegate;

	public SimpleBoundedMap(int maxSize) {
		this(new LinkedHashMap<K, V>(), maxSize);
	}

	public SimpleBoundedMap(Map<K, V> delegate, int maxSize) {
		this(delegate, maxSize, new LinkedList<K>());
	}

	protected SimpleBoundedMap(Map<K, V> delegate, int maxSize, Queue<K> keys) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = keys;
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
		keys.remove(key);
		return delegate.remove(key);
	}

	@Override
	public void clear() {
		keys.clear();
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
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public int getMaxSize() {
		return maxSize;
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
				eldestKey = keys.poll();
				eldestValue = delegate.remove(eldestKey);
			}
		}
		if (reached) {
			onEviction(eldestKey, eldestValue);
		}
	}

}
