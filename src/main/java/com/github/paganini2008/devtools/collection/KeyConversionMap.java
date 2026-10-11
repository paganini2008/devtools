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

/**
 * 
 * KeyConversionMap
 * <p>
 * Base class of maps which look up keys by a converted form (e.g. lower case),
 * while keeping the most recently put original key. Two keys with the same
 * converted form are regarded as the same key.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public abstract class KeyConversionMap<T, K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<K, V> delegate;
	private final Map<T, K> keys;

	protected KeyConversionMap(Map<K, V> delegate) {
		this.delegate = delegate;
		this.keys = MapUtils.synchronizedHashMap();
		for (K key : delegate.keySet()) {
			keys.put(convertKey(key), key);
		}
	}

	@Override
	public boolean containsKey(Object key) {
		T convertedKey = convertKey(key);
		return keys.containsKey(convertedKey) && delegate.containsKey(keys.get(convertedKey));
	}

	@Override
	public V get(Object key) {
		T convertedKey = convertKey(key);
		return keys.containsKey(convertedKey) ? delegate.get(keys.get(convertedKey)) : null;
	}

	@Override
	public V put(K key, V value) {
		T convertedKey = convertKey(key);
		if (keys.containsKey(convertedKey)) {
			K originalKey = keys.get(convertedKey);
			if (originalKey == null ? key == null : originalKey.equals(key)) {
				return delegate.put(key, value);
			}
			V previous = delegate.remove(originalKey);
			keys.put(convertedKey, key);
			delegate.put(key, value);
			return previous;
		}
		keys.put(convertedKey, key);
		return delegate.put(key, value);
	}

	@Override
	public V remove(Object key) {
		T convertedKey = convertKey(key);
		if (keys.containsKey(convertedKey)) {
			return delegate.remove(keys.remove(convertedKey));
		}
		return null;
	}

	@Override
	public void clear() {
		keys.clear();
		delegate.clear();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySetView<K, V>(delegate, this::remove);
	}

	/**
	 * Converts the given key into the form used for lookup.
	 * 
	 * @param key
	 *            the key, may be null
	 * @return the converted key
	 */
	protected abstract T convertKey(Object key);

}
