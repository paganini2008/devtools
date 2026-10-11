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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.ObjectUtils;

/**
 * 
 * AtomicMutableMap
 * <p>
 * A map holding each value in an {@link java.util.concurrent.atomic.AtomicStampedReference}
 * so that {@link #merge(Object, Object, BiFunction)} can update values
 * atomically without locking. Keys are transformed by {@link #mutate(Object)}
 * before use (identity by default).
 *
 * @author Fred Feng
 * @date 2021-12-01
 * @since 2.0.4
 */
public abstract class AtomicMutableMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;

	protected final Map<K, AtomicStampedReference<V>> delegate;

	protected AtomicMutableMap(Map<K, AtomicStampedReference<V>> delegate) {
		this.delegate = delegate;
	}

	@Override
	public V get(Object key) {
		AtomicStampedReference<V> ref = delegate.get(mutate(key));
		return ref != null ? ref.getReference() : null;
	}

	@Override
	public V put(K key, V value) {
		AtomicStampedReference<V> prev = delegate.put(mutate(key), new AtomicStampedReference<V>(value, 0));
		return prev != null ? prev.getReference() : null;
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(mutate(key));
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
	public V remove(Object key) {
		AtomicStampedReference<V> prev = delegate.remove(mutate(key));
		return prev != null ? prev.getReference() : null;
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public Collection<V> values() {
		return delegate.values().stream().map(ref -> ref.getReference()).collect(Collectors.toList());
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	/**
	 * Returns a snapshot of this map as a {@link LinkedHashMap}.
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

	/**
	 * Atomically updates the value of the key with the given function. Note
	 * that, unlike {@link Map#merge(Object, Object, BiFunction)}, the function
	 * is also invoked (with a null current value) when the key is absent, and
	 * a null result is stored rather than removing the key.
	 * 
	 * @return the updated value
	 */
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> fun) {
		key = mutate(key);
		AtomicStampedReference<V> ref = delegate.get(key);
		if (ref == null) {
			delegate.putIfAbsent(key, new AtomicStampedReference<V>(null, 0));
			ref = delegate.get(key);
		}
		if (ref != null) {
			V current;
			V update;
			final int[] stampHolder = new int[1];
			do {
				current = ref.get(stampHolder);
				update = fun.apply(current, value);
			} while (!ref.compareAndSet(current, update, stampHolder[0], stampHolder[0] + 1));
			return update;
		}
		return null;
	}

	/**
	 * Transforms the given key into the key actually stored.
	 * 
	 * @param key
	 *            the key given by the caller
	 * @return the stored key, the given key itself by default
	 */
	@SuppressWarnings("unchecked")
	protected K mutate(Object key) {
		return (K) key;
	}

}
