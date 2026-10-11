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
import java.util.Map;
import java.util.Set;

/**
 * 
 * MutableMap
 * <p>
 * Base class of maps which transform every key by {@link #mutate(Object)}
 * before storing or looking it up, e.g. rounding a timestamp to a time slot.
 *
 * @author Fred Feng
 * @date 2021-11-30
 * @since 2.0.4
 */
public abstract class MutableMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;

	protected final Map<K, V> delegate;

	protected MutableMap(Map<K, V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public V get(Object key) {
		return delegate.get(mutate(key));
	}

	@Override
	public V put(K key, V value) {
		return delegate.put(mutate(key), value);
	}
	
	@Override
	public V putIfAbsent(K key, V value) {
		return delegate.putIfAbsent(mutate(key), value);
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(mutate(key));
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(mutate(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
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
	public int size() {
		return delegate.size();
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	/**
	 * Transforms the given key into the key actually stored.
	 * 
	 * @param key
	 *            the key given by the caller
	 * @return the stored key
	 */
	protected abstract K mutate(Object key);

}
