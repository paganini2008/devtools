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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * LruMap
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 * @see LinkedHashMap
 */
public class LruMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, BoundedMap<K, V> {

	private static final long serialVersionUID = -1958272189245104075L;

	public LruMap() {
		this(128);
	}

	public LruMap(final int maxSize) {
		this(false, maxSize);
	}

	public LruMap(final boolean sorted, final int maxSize) {
		this(sorted, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	public LruMap(final boolean sorted, final int maxSize, final LruMapSupplier<K, Object> supplier) {
		this(sorted ? new ConcurrentSkipListMap<K, V>() : new ConcurrentHashMap<K, V>(), maxSize, supplier);
	}

	public LruMap(final Map<K, V> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	public LruMap(final Map<K, V> delegate, final int maxSize, final LruMapSupplier<K, Object> supplier) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = supplier.get(maxSize, (key, value) -> {
			V eldestValue = delegate.remove(key);
			onEviction(key, eldestValue);
		});
	}

	private final Map<K, V> delegate;
	private final Map<K, Object> keys;
	private final int maxSize;

	@Override
	public V get(Object key) {
		keys.get(key);
		return delegate.get(key);
	}

	@Override
	public V put(K key, V value) {
		keys.put(key, key);
		return delegate.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		keys.putIfAbsent(key, key);
		return delegate.putIfAbsent(key, value);
	}

	@Override
	public V remove(Object key) {
		keys.remove(key);
		return delegate.remove(key);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public void clear() {
		keys.clear();
		delegate.clear();
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public boolean containsKey(Object key) {
		keys.get(key);
		return delegate.containsKey(key);
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
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public Map<K, V> getDelegate() {
		return delegate;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

}
