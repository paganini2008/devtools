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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Comparables;

/**
 * 
 * KeyMatchedMap
 *
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public abstract class KeyMatchedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;
	private final Map<K, V> delegate;
	private final Set<K> keys;

	protected KeyMatchedMap(final Map<K, V> delegate, final boolean matchFirst) {
		this.delegate = delegate;
		keys = Collections.synchronizedNavigableSet(new TreeSet<K>(new Comparator<K>() {

			public int compare(K left, K right) {
				return matchFirst ? Comparables.compareTo(left, right) : Comparables.compareTo(right, left);
			}

		}));
	}

	@Override
	public boolean containsKey(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.containsKey(key) : false;
	}

	public boolean existsKey(Object inputKey) {
		return delegate.containsKey(inputKey);
	}

	@Override
	public V get(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.get(key) : null;
	}

	@Override
	public V put(K key, V value) {
		Assert.isNull(key, "Nullable key");
		keys.add(key);
		return delegate.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V v = null;
		if (!existsKey(key)) {
			v = put(key, value);
		}
		return v;
	}

	@Override
	public V remove(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.remove(key) : null;
	}

	@Override
	public void clear() {
		keys.clear();
		delegate.clear();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	private K matches(Object inputKey) {
		for (K key : keys) {
			if (match(key, inputKey)) {
				return key;
			}
		}
		return null;
	}

	protected abstract boolean match(K key, Object inputKey);
}
