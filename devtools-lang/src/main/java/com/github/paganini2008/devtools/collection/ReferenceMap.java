/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 
 * ReferenceMap
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class ReferenceMap<K, V> implements Map<K, V> {

	private final Map<K, Reference<V>> delegate;

	protected ReferenceMap(Map<K, Reference<V>> delegate) {
		this.delegate = delegate;
	}

	protected abstract Reference<V> fold(K key, V value);

	private V unfold(Reference<V> ref) {
		if (ref == null) {
			return null;
		}
		return ref.get();
	}

	public int size() {
		return delegate.size();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
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
			delegate.put(entry.getKey(), fold(entry.getKey(), entry.getValue()));
		}
	}

	public void clear() {
		delegate.clear();
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public String toString() {
		return delegate.toString();
	}

	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
}
