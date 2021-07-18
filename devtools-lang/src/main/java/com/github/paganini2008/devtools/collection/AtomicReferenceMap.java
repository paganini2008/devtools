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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicStampedReference;

import com.github.paganini2008.devtools.ObjectUtils;

/**
 * 
 * AtomicReferenceMap
 *
 * @author Fred Feng
 * @version 1.0
 */
public class AtomicReferenceMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 4925536519828538578L;

	private final Map<K, AtomicStampedReference<V>> real;

	public AtomicReferenceMap() {
		this(false);
	}

	public AtomicReferenceMap(boolean ordered) {
		this(ordered ? new ConcurrentSkipListMap<K, AtomicStampedReference<V>>() : new ConcurrentHashMap<K, AtomicStampedReference<V>>());
	}

	public AtomicReferenceMap(Map<K, AtomicStampedReference<V>> real) {
		this.real = real;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		AtomicStampedReference<V> ref = real.get(key);
		if (ref == null) {
			real.putIfAbsent(key, new AtomicStampedReference<V>(null, 0));
			ref = real.get(key);
		}
		V current;
		V update;
		do {
			current = ref.getReference();
			update = merge(key, current, value);
		} while (!ref.compareAndSet(current, update, ref.getStamp(), ref.getStamp() + 1));
		return update;
	}

	@Override
	public V put(K key, V value) {
		AtomicStampedReference<V> eldest = real.put(key, new AtomicStampedReference<V>(value, 0));
		return eldest != null ? eldest.getReference() : null;
	}

	protected V merge(K key, V current, V value) {
		return value;
	}

	@Override
	public V get(Object key) {
		AtomicStampedReference<V> ref = real.get(key);
		return ref != null ? ref.getReference() : null;
	}

	@Override
	public int size() {
		return real.size();
	}

	@Override
	public void clear() {
		real.clear();
	}

	@Override
	public Set<K> keySet() {
		return real.keySet();
	}

	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<V>();
		for (AtomicStampedReference<V> ref : real.values()) {
			values.add(ref.getReference());
		}
		return values;
	}

	@Override
	public V remove(Object key) {
		AtomicStampedReference<V> ref = real.remove(key);
		return ref != null ? ref.getReference() : null;
	}

	@Override
	public boolean containsValue(Object value) {
		for (AtomicStampedReference<V> ref : real.values()) {
			if (ObjectUtils.equals(ref.getReference(), value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return real.containsKey(key);
	}

	public Map<K, V> toMap() {
		Map<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, AtomicStampedReference<V>> entry : real.entrySet()) {
			map.put(entry.getKey(), entry.getValue().getReference());
		}
		return map;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return toMap().entrySet();
	}

	@Override
	public String toString() {
		return toMap().toString();
	}

}
