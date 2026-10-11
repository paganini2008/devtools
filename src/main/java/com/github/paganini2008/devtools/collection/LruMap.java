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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * LruMap
 * <p>
 * A thread-safe map bounded by a maximum size which evicts the least recently
 * used entry when the size is exceeded. Subclasses can override
 * {@link #onEviction(Object, Object)} to be notified of evicted entries.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 * @see java.util.LinkedHashMap
 */
public class LruMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, BoundedMap<K, V> {

	private static final long serialVersionUID = -1958272189245104075L;

	/**
	 * Creates a LruMap holding at most 128 entries.
	 */
	public LruMap() {
		this(128);
	}

	/**
	 * Creates a LruMap backed by a {@link ConcurrentHashMap}.
	 * 
	 * @param maxSize
	 *            the maximum number of entries, must be greater than zero
	 */
	public LruMap(final int maxSize) {
		this(false, maxSize);
	}

	/**
	 * Creates a LruMap.
	 * 
	 * @param sorted
	 *            true to back the map by a {@link ConcurrentSkipListMap}
	 *            (sorted by key), false by a {@link ConcurrentHashMap}
	 * @param maxSize
	 *            the maximum number of entries, must be greater than zero
	 */
	public LruMap(final boolean sorted, final int maxSize) {
		this(sorted, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	public LruMap(final boolean sorted, final int maxSize, final LruMapSupplier<K, Object> supplier) {
		this(sorted ? new ConcurrentSkipListMap<K, V>() : new ConcurrentHashMap<K, V>(), maxSize, supplier);
	}

	public LruMap(final Map<K, V> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	/**
	 * Creates a LruMap with a custom key tracker.
	 * 
	 * @param delegate
	 *            the map which actually holds the entries
	 * @param maxSize
	 *            the maximum number of entries
	 * @param supplier
	 *            creates the access-ordered map used to track the usage of
	 *            keys; it must invoke the given eviction listener when its
	 *            eldest key is dropped
	 */
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
		synchronized (keys) {
			keys.get(key);
			return delegate.get(key);
		}
	}

	@Override
	public V put(K key, V value) {
		synchronized (keys) {
			V previous = delegate.put(key, value);
			keys.put(key, key);
			return previous;
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		synchronized (keys) {
			V previous = delegate.putIfAbsent(key, value);
			if (previous == null) {
				keys.put(key, key);
			} else {
				keys.get(key);
			}
			return previous;
		}
	}

	@Override
	public V remove(Object key) {
		synchronized (keys) {
			keys.remove(key);
			return delegate.remove(key);
		}
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
		synchronized (keys) {
			keys.clear();
			delegate.clear();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public boolean containsKey(Object key) {
		synchronized (keys) {
			keys.get(key);
			return delegate.containsKey(key);
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySetView<K, V>(delegate, this::remove);
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
