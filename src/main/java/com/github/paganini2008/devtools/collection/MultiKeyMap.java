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
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * MultiKeyMap
 * <p>
 * A map which allows duplicated keys: every {@link #put(Object, Object)}
 * appends a new mapping, and lookups return the first mapping of a key.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class MultiKeyMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 519272736906601965L;

	private final List<K> keys;
	private final Map<Integer, V> values;

	public MultiKeyMap() {
		this(new ArrayList<K>(), new HashMap<Integer, V>());
	}

	protected MultiKeyMap(List<K> keys, Map<Integer, V> values) {
		this.keys = keys;
		this.values = values;
	}

	/**
	 * Appends a new mapping. An existing mapping of an equal key is kept, so
	 * the same key can be added multiple times.
	 * 
	 * @return always null as no mapping is replaced
	 */
	public synchronized V put(K key, V value) {
		keys.add(key);
		return values.put(keys.size() - 1, value);
	}

	public int size() {
		return values.size();
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	/**
	 * Returns the value of the first mapping of the key.
	 */
	public synchronized V get(Object key) {
		int keyIndex = keys.indexOf(key);
		return keyIndex != -1 ? values.get(keyIndex) : null;
	}

	/**
	 * Removes the first mapping of the key.
	 */
	public synchronized V remove(Object key) {
		int keyIndex = keys.indexOf(key);
		if (keyIndex == -1) {
			return null;
		}
		keys.remove(keyIndex);
		V value = values.remove(keyIndex);
		int size = keys.size();
		for (int i = keyIndex; i < size; i++) {
			values.put(i, values.remove(i + 1));
		}
		return value;
	}

	/**
	 * Removes all mappings of the key.
	 * 
	 * @param key
	 *            the key to remove
	 */
	public synchronized void removeAll(Object key) {
		while (keys.contains(key)) {
			remove(key);
		}
	}

	public synchronized void clear() {
		keys.clear();
		values.clear();
	}

	/**
	 * Returns a view of the keys in insertion order, which may contain
	 * duplicated keys.
	 */
	public Set<K> keySet() {
		return new ViewSet<K>(keys);
	}

	public Collection<V> values() {
		return values.values();
	}

	/**
	 * Returns a snapshot of the mappings in insertion order.
	 */
	public synchronized Set<Map.Entry<K, V>> entrySet() {
		List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>();
		for (int i = 0, size = keys.size(); i < size; i++) {
			list.add(new CombinedEntry<K, V>(keys.get(i), new IndexedEntry(i)));
		}
		return new ViewSet<Map.Entry<K, V>>(list);
	}

	private class IndexedEntry implements Map.Entry<Integer, V>, Serializable {

		private static final long serialVersionUID = 1L;
		private final Integer index;

		IndexedEntry(Integer index) {
			this.index = index;
		}

		public Integer getKey() {
			return index;
		}

		public V getValue() {
			return values.get(index);
		}

		public V setValue(V value) {
			return values.put(index, value);
		}

	}

	/**
	 * 
	 * ViewSet
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class ViewSet<T> extends AbstractSet<T> implements Serializable {

		private static final long serialVersionUID = 1L;

		private final Collection<T> c;

		ViewSet(Collection<T> c) {
			this.c = c;
		}

		public Iterator<T> iterator() {
			return c.iterator();
		}

		public int size() {
			return c.size();
		}

	}

	/**
	 * 
	 * CombinedEntry
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class CombinedEntry<K, V> implements Map.Entry<K, V>, Serializable {

		private static final long serialVersionUID = 1L;
		final K key;
		final Map.Entry<Integer, V> entry;

		CombinedEntry(K key, Entry<Integer, V> entry) {
			this.key = key;
			this.entry = entry;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return entry.getValue();
		}

		public V setValue(V value) {
			return entry.setValue(value);
		}

		public String toString() {
			return getKey() + "=" + getValue();
		}

	}

	/**
	 * Creates a thread-safe MultiKeyMap.
	 * 
	 * @return a new MultiKeyMap
	 */
	public static <K, V> Map<K, V> synchronizedMap() {
		return new MultiKeyMap<K, V>(new CopyOnWriteArrayList<K>(), new ConcurrentHashMap<Integer, V>());
	}
}
