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
 * <p>
 * Base class of maps which look up values by matching the input key against
 * the stored keys (e.g. by prefix or wildcard) instead of by equality. The
 * stored keys are tried in ascending order, or descending order if
 * <code>matchFirst</code> is false.
 *
 * @author Fred Feng
 * @date 2020-12-18
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

	/**
	 * Returns true if the input key matches any stored key.
	 */
	@Override
	public boolean containsKey(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.containsKey(key) : false;
	}

	/**
	 * Returns true if the given key is stored exactly (without matching).
	 * 
	 * @param inputKey
	 *            the key to look up
	 * @return true if the key exists
	 */
	public boolean existsKey(Object inputKey) {
		return delegate.containsKey(inputKey);
	}

	/**
	 * Returns the value of the first stored key which matches the input key.
	 */
	@Override
	public V get(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.get(key) : null;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the key is null
	 */
	@Override
	public V put(K key, V value) {
		Assert.isNull(key, "Nullable key");
		synchronized (keys) {
			keys.add(key);
			return delegate.put(key, value);
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		synchronized (keys) {
			if (!existsKey(key)) {
				return put(key, value);
			}
			return delegate.get(key);
		}
	}

	/**
	 * Removes the first stored key which matches the input key.
	 */
	@Override
	public V remove(Object inputKey) {
		synchronized (keys) {
			K key = matches(inputKey);
			if (key != null) {
				keys.remove(key);
				return delegate.remove(key);
			}
			return null;
		}
	}

	@Override
	public void clear() {
		synchronized (keys) {
			keys.clear();
			delegate.clear();
		}
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySetView<K, V>(delegate, key -> {
			synchronized (keys) {
				keys.remove(key);
				delegate.remove(key);
			}
		});
	}

	private K matches(Object inputKey) {
		synchronized (keys) {
			for (K key : keys) {
				if (match(key, inputKey)) {
					return key;
				}
			}
		}
		return null;
	}

	/**
	 * Tests whether a stored key matches the input key.
	 * 
	 * @param key
	 *            the stored key
	 * @param inputKey
	 *            the key given by the caller
	 * @return true if matched
	 */
	protected abstract boolean match(K key, Object inputKey);

}
