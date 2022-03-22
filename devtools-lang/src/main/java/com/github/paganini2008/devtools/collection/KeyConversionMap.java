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
import java.util.Map;
import java.util.Set;

/**
 * 
 * KeyConversionMap
 *
 * @author Fred Feng
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
	}

	public boolean containsKey(Object key) {
		Object realKey = keys.get(convertKey(key));
		return delegate.containsKey(realKey);
	}

	public V get(Object key) {
		Object realKey = keys.get(convertKey(key));
		return realKey != null ? delegate.get(realKey) : null;
	}

	public V put(K key, V value) {
		keys.put(convertKey(key), key);
		return delegate.put(key, value);
	}

	public V remove(Object key) {
		Object realKey = keys.remove(convertKey(key));
		return realKey != null ? delegate.remove(realKey) : null;
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	protected abstract T convertKey(Object key);
}
