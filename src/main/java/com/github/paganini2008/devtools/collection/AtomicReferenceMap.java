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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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
 * <p>
 * A map holding each value in an {@link AtomicStampedReference}, whose
 * {@link #putIfAbsent(Object, Object)} atomically combines the current value
 * and the given value via the overridable {@link #merge(Object, Object, Object)}
 * hook.
 *
 * @author Fred Feng
 * @date 2021-01-22
 * @since 2.0.1
 */
public class AtomicReferenceMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 4925536519828538578L;

	private final Map<K, AtomicStampedReference<V>> delegate;

	public AtomicReferenceMap() {
		this(false);
	}

	/**
	 * Creates an AtomicReferenceMap.
	 * 
	 * @param ordered
	 *            true to keep keys sorted (backed by a
	 *            {@link ConcurrentSkipListMap}), false to use a
	 *            {@link ConcurrentHashMap}
	 */
	public AtomicReferenceMap(boolean ordered) {
		this(ordered ? new ConcurrentSkipListMap<K, AtomicStampedReference<V>>() : new ConcurrentHashMap<K, AtomicStampedReference<V>>());
	}

	public AtomicReferenceMap(Map<K, AtomicStampedReference<V>> delegate) {
		this.delegate = delegate;
	}

	/**
	 * Atomically replaces the current value of the key with
	 * <code>merge(key, current, value)</code>. Note that, unlike
	 * {@link Map#putIfAbsent(Object, Object)}, the default hook always stores
	 * the given value, and the <b>new</b> value is returned.
	 * 
	 * @return the value stored by this call
	 */
	@Override
	public V putIfAbsent(K key, V value) {
		AtomicStampedReference<V> ref = delegate.get(key);
		if (ref == null) {
			delegate.putIfAbsent(key, new AtomicStampedReference<V>(null, 0));
			ref = delegate.get(key);
		}
		V current;
		V update;
		final int[] stampHolder = new int[1];
		do {
			current = ref.get(stampHolder);
			update = merge(key, current, value);
		} while (!ref.compareAndSet(current, update, stampHolder[0], stampHolder[0] + 1));
		return update;
	}

	@Override
	public V put(K key, V value) {
		AtomicStampedReference<V> eldest = delegate.put(key, new AtomicStampedReference<V>(value, 0));
		return eldest != null ? eldest.getReference() : null;
	}

	/**
	 * Combines the current value and the given value, used by
	 * {@link #putIfAbsent(Object, Object)}.
	 * 
	 * @param key
	 *            the key
	 * @param current
	 *            the current value, may be null
	 * @param value
	 *            the given value
	 * @return the value to store, the given value by default
	 */
	protected V merge(K key, V current, V value) {
		return value;
	}

	@Override
	public V get(Object key) {
		AtomicStampedReference<V> ref = delegate.get(key);
		return ref != null ? ref.getReference() : null;
	}

	@Override
	public int size() {
		return delegate.size();
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
		List<V> values = new ArrayList<V>();
		for (AtomicStampedReference<V> ref : delegate.values()) {
			values.add(ref.getReference());
		}
		return values;
	}

	@Override
	public V remove(Object key) {
		AtomicStampedReference<V> ref = delegate.remove(key);
		return ref != null ? ref.getReference() : null;
	}

	@Override
	public boolean containsValue(Object value) {
		for (AtomicStampedReference<V> ref : delegate.values()) {
			if (ObjectUtils.equals(ref.getReference(), value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	/**
	 * Returns a snapshot of this map, keeping the iteration order.
	 * 
	 * @return a new map containing the current mappings
	 */
	public Map<K, V> toMap() {
		Map<K, V> map = new LinkedHashMap<K, V>();
		for (Map.Entry<K, AtomicStampedReference<V>> entry : delegate.entrySet()) {
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
