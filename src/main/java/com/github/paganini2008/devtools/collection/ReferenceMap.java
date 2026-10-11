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

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * ReferenceMap
 * <p>
 * Base class of maps whose values are held by {@link Reference references}, so
 * that they can be reclaimed by the garbage collector. A key whose value has
 * been reclaimed behaves as if it was absent, although it may still be counted
 * by {@link #size()} until it is purged. {@link #values()} and
 * {@link #entrySet()} return snapshots of the live mappings.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class ReferenceMap<K, V> implements Map<K, V> {

	private final Map<K, Reference<V>> delegate;

	protected ReferenceMap(Map<K, Reference<V>> delegate) {
		this.delegate = delegate;
	}

	/**
	 * Wraps the value into a reference.
	 * 
	 * @param key
	 *            the key of the value
	 * @param value
	 *            the value to wrap
	 * @return the reference holding the value
	 */
	protected abstract Reference<V> fold(K key, V value);

	private V unfold(Reference<V> ref) {
		if (ref == null) {
			return null;
		}
		return ref.get();
	}

	/**
	 * Removes the mapping only if the key is still mapped to the given
	 * reference.
	 * 
	 * @param key
	 *            the key
	 * @param ref
	 *            the expected reference
	 * @return true if removed
	 */
	protected boolean removeReference(K key, Reference<V> ref) {
		return delegate.remove(key, ref);
	}

	public int size() {
		return delegate.size();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public boolean containsKey(Object key) {
		return unfold(delegate.get(key)) != null;
	}

	public boolean containsValue(Object value) {
		if (value == null) {
			return false;
		}
		for (Reference<V> ref : delegate.values()) {
			if (Objects.equals(value, unfold(ref))) {
				return true;
			}
		}
		return false;
	}

	public V get(Object key) {
		return unfold(delegate.get(key));
	}

	public V put(K key, V value) {
		return unfold(delegate.put(key, fold(key, value)));
	}

	public V remove(Object key) {
		return unfold(delegate.remove(key));
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		delegate.clear();
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public String toString() {
		return toMap().toString();
	}

	public Collection<V> values() {
		List<V> values = new ArrayList<V>();
		V value;
		for (Reference<V> ref : delegate.values()) {
			if ((value = unfold(ref)) != null) {
				values.add(value);
			}
		}
		return Collections.unmodifiableList(values);
	}

	public Set<Entry<K, V>> entrySet() {
		return Collections.unmodifiableMap(toMap()).entrySet();
	}

	private Map<K, V> toMap() {
		Map<K, V> map = new LinkedHashMap<K, V>();
		V value;
		for (Entry<K, Reference<V>> entry : delegate.entrySet()) {
			if ((value = unfold(entry.getValue())) != null) {
				map.put(entry.getKey(), value);
			}
		}
		return map;
	}

}
