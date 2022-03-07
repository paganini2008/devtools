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
 *
 * @author Fred Feng
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

	public Map<K, V> toMap() {
		return delegate.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getReference(), (o, n) -> o, LinkedHashMap::new));
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return toMap().entrySet();
	}

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
			do {
				current = ref.getReference();
				update = fun.apply(current, value);
			} while (!ref.compareAndSet(current, update, ref.getStamp(), ref.getStamp() + 1));
			return update;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected K mutate(Object key) {
		return (K) key;
	}

}
