/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * MultiKeyMap
 * 
 * @author Fred Feng
 * @version 1.0
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

	public V put(K key, V value) {
		keys.add(key);
		return values.put(keys.lastIndexOf(key), value);
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

	public V get(Object key) {
		int keyIndex = keys.indexOf(key);
		return keyIndex != -1 ? values.get(keyIndex) : null;
	}

	public V remove(Object key) {
		if (keys.remove(key)) {
			return values.remove(key);
		}
		return null;
	}

	public void removeAll(Object key) {
		while (remove(key) != null) {
			;
		}
	}

	public void clear() {
		keys.clear();
		values.clear();
	}

	public Set<K> keySet() {
		return new ViewSet<K>(keys);
	}

	public Collection<V> values() {
		return values.values();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>();
		int i = 0;
		Iterator<Map.Entry<Integer, V>> valueIterator = values.entrySet().iterator();
		while (i < keys.size() && valueIterator.hasNext()) {
			list.add(new CombinedEntry<K, V>(keys.get(i++), valueIterator.next()));
		}
		return new ViewSet<Map.Entry<K, V>>(list);
	}

	/**
	 * 
	 * ViewSet
	 * 
	 * @author Fred Feng
	 * @version 1.0
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
	 * @version 1.0
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

	public static <K, V> Map<K, V> synchronizedMap() {
		return new MultiKeyMap<K, V>(new CopyOnWriteArrayList<K>(), new ConcurrentHashMap<Integer, V>());
	}

	public static void main(String[] args) {
		Map<String, Long> map = new MultiKeyMap<>();
		map.put("123", 1L);
		map.put("123", 1L);
		map.put("123", 3L);
		map.put("234", 2L);
		System.out.println(map.keySet());
		System.out.println(map.get("234"));

		map = new IdentityHashMap<>();

		map.put("123", 1L);
		map.put("123", 1L);
		map.put("123", 3L);
		map.put("234", 2L);
		System.out.println(map);
	}

}
