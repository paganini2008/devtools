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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.MissingKeyException;
import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * MapUtils
 * <p>
 * Null-safe utility methods for maps and properties.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class MapUtils {

	/**
	 * Returns the immutable empty map.
	 */
	public static <K, V> Map<K, V> emptyMap() {
		return Collections.EMPTY_MAP;
	}

	/**
	 * Returns an immutable entry.
	 *
	 * @throws IllegalArgumentException if the key is null
	 */
	public static <K, V> Map.Entry singletonEntry(K key, V value) {
		return new SingletonEntry(key, value);
	}

	/**
	 * Returns a synchronized HashMap.
	 */
	public static <K, V> Map<K, V> synchronizedHashMap(int initialCapacity, float loadFactor) {
		return Collections.synchronizedMap(new HashMap<>(initialCapacity, loadFactor));
	}

	/**
	 * Returns a synchronized HashMap.
	 */
	public static <K, V> Map<K, V> synchronizedHashMap(int initialCapacity) {
		return Collections.synchronizedMap(new HashMap<>(initialCapacity, 0.75F));
	}

	/**
	 * Returns a synchronized HashMap.
	 */
	public static <K, V> Map<K, V> synchronizedHashMap() {
		return Collections.synchronizedMap(new HashMap<>());
	}

	/**
	 * Returns a synchronized HashMap copied from the given map.
	 */
	public static <K, V> Map<K, V> synchronizedHashMap(Map<K, V> m) {
		return Collections.synchronizedMap(new HashMap<>(m));
	}

	/**
	 * Returns a synchronized LinkedHashMap.
	 */
	public static <K, V> Map<K, V> synchronizedLinkedHashMap() {
		return Collections.synchronizedMap(new LinkedHashMap<>());
	}

	/**
	 * Returns a synchronized LinkedHashMap copied from the given map.
	 */
	public static <K, V> Map<K, V> synchronizedLinkedHashMap(Map<K, V> m) {
		return Collections.synchronizedMap(new LinkedHashMap<>(m));
	}

	/**
	 * Returns a synchronized LinkedHashMap, ordered by access if <code>accessOrder</code> is true.
	 */
	public static <K, V> Map<K, V> synchronizedLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		return Collections.synchronizedMap(new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder));
	}

	/**
	 * Returns a synchronized, access-ordered LinkedHashMap which drops its least
	 * recently used entry when the size exceeds <code>maxSize</code>.
	 *
	 * @param evictionListener
	 *            notified of every dropped entry, may be null
	 * @throws IllegalArgumentException if maxSize is less than 1
	 */
	public static <K, V> Map<K, V> synchronizedLinkedHashMap(final int initialSize, final int maxSize,
			final EvictionListener<K, V> evictionListener) {
		if (maxSize < 1) {
			throw new IllegalArgumentException("MaxSize must greater than zero");
		}
		return Collections.synchronizedMap(new LinkedHashMap<K, V>(initialSize, 0.75F, true) {
			private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(Map.Entry<K, V> eldestEntry) {
				boolean result;
				if (result = size() > maxSize) {
					if (evictionListener != null) {
						evictionListener.onEviction(eldestEntry.getKey(), eldestEntry.getValue());
					}
				}
				return result;
			}
		});
	}

	/**
	 * Returns an immutable map holding only the first entry of the map.
	 *
	 * @throws IllegalArgumentException if the map is empty
	 */
	public static <K, V> Map<K, V> singletonMap(Map<K, V> map) {
		return singletonMap(map, true);
	}

	/**
	 * Returns an immutable map holding only the first (or last) entry of the map.
	 *
	 * @throws IllegalArgumentException if the map is empty
	 */
	public static <K, V> Map<K, V> singletonMap(Map<K, V> map, boolean firstEntry) {
		Map.Entry<K, V> entry = firstEntry ? getFirstEntry(map) : getLastEntry(map);
		return singletonMap(entry);
	}

	/**
	 * Returns an immutable map holding only the entry.
	 *
	 * @throws IllegalArgumentException if the entry is null
	 */
	public static <K, V> Map<K, V> singletonMap(Map.Entry<K, V> entry) {
		Assert.isNull(entry);
		return Collections.singletonMap(entry.getKey(), entry.getValue());
	}

	/**
	 * Checks if the object is a Map (false for null).
	 */
	public static boolean isMap(Object obj) {
		return obj == null ? false : obj instanceof Map;
	}

	public static boolean isNotMap(Object obj) {
		return !isMap(obj);
	}

	/**
	 * Returns the non-null value of the key.
	 *
	 * @throws com.github.paganini2008.devtools.MissingKeyException if the value is null or absent
	 */
	public static Object getIfRequired(Map<String, Object> map, String key) {
		return getIfRequired(map, key, new MissingKeyException(key));
	}

	/**
	 * Returns the non-null value of the key, or throws the given exception if the
	 * map is null or the value is null or absent.
	 */
	public static <K, V> V getIfRequired(Map<K, V> map, K key, RuntimeException e) {
		V value = null;
		if (map != null) {
			value = map.get(key);
		}
		if (value == null) {
			throw e;
		}
		return value;
	}

	/**
	 * Returns the value of the key, or null if the map is null.
	 */
	public static <K, V> V get(Map<K, V> map, K key) {
		return getOrDefault(map, key, (V) null);
	}

	/**
	 * Returns the value of the key, or the default value if the map is null or the
	 * value is null.
	 *
	 * <pre>
	 * MapUtils.getOrDefault({a=1}, "a", "d") = "1"
	 * MapUtils.getOrDefault({a=1}, "z", "d") = "d"
	 * MapUtils.getOrDefault(null, "z", "d")  = "d"
	 * </pre>
	 */
	public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
		if (map != null) {
			V value;
			if ((value = map.get(key)) == null) {
				value = defaultValue;
			}
			return value;
		}
		return defaultValue;
	}

	/**
	 * Returns the value of the key, or the supplied value if the map is null or the value is null.
	 */
	public static <K, V> V getOrDefault(Map<K, V> map, K key, Supplier<V> supplier) {
		if (map != null) {
			V value;
			if ((value = map.get(key)) == null) {
				value = supplier.get();
			}
			return value;
		}
		return supplier.get();
	}

	/**
	 * Returns the value of the key, or the value computed from the key if the map is null or the value is null.
	 */
	public static <K, V> V getOrDefault(Map<K, V> map, K key, Function<K, V> function) {
		if (map != null) {
			V value;
			if ((value = map.get(key)) == null) {
				value = function.apply(key);
			}
			return value;
		}
		return function.apply(key);
	}

	/**
	 * Returns the value of the key, creating and storing it with the supplier if it
	 * is absent. Creation is atomic for a {@link java.util.concurrent.ConcurrentMap}
	 * and for maps synchronized on themselves (such as
	 * {@link java.util.Collections#synchronizedMap(Map)}).
	 *
	 * @return the current value, or null if the map is null
	 */
	public static <K, V> V get(Map<K, V> map, K key, Supplier<V> supplier) {
		if (map != null) {
			V value = map.get(key);
			if (value == null) {
				if (map instanceof ConcurrentMap) {
					return map.computeIfAbsent(key, k -> supplier.get());
				}
				synchronized (map) {
					value = map.get(key);
					if (value == null) {
						map.putIfAbsent(key, supplier.get());
					}
					value = map.get(key);
				}
			}
			return value;
		}
		return null;
	}

	/**
	 * Returns the value of the key, computing and storing it with the function if
	 * it is absent. See {@link #get(Map, Object, Supplier)} for atomicity.
	 *
	 * @return the current value, or null if the map is null
	 */
	public static <K, V> V get(Map<K, V> map, K key, Function<K, V> function) {
		if (map != null) {
			V value = map.get(key);
			if (value == null) {
				if (map instanceof ConcurrentMap) {
					return map.computeIfAbsent(key, function);
				}
				synchronized (map) {
					value = map.get(key);
					if (value == null) {
						map.putIfAbsent(key, function.apply(key));
					}
					value = map.get(key);
				}
			}
			return value;
		}
		return null;
	}

	/**
	 * Flattens the entries into a list of key1, value1, key2, value2 and so on.
	 */
	public static <E> List<E> toList(Map<E, E> map) {
		if (isEmpty(map)) {
			return ListUtils.emptyList();
		}
		List<E> list = new ArrayList<E>();
		for (Map.Entry<E, E> e : map.entrySet()) {
			list.add(e.getKey());
			list.add(e.getValue());
		}
		return list;
	}

	/**
	 * Copies the entries into a new list, or returns an empty list if the map is null or empty.
	 */
	public static <K, V> List<Map.Entry<K, V>> toEntries(Map<K, V> map) {
		if (isEmpty(map)) {
			return ListUtils.emptyList();
		}
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}

	/**
	 * Returns the first entry in iteration order, or null if the map is empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> Entry<K, V> getFirstEntry(Map<K, V> map) {
		Assert.isNull(map, "Map must not be null.");
		return CollectionUtils.getFirst(map.entrySet().iterator());
	}

	/**
	 * Returns the last entry in iteration order, or null if the map is empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> Entry<K, V> getLastEntry(Map<K, V> map) {
		Assert.isNull(map, "Map must not be null.");
		return CollectionUtils.getLast(map.entrySet().iterator());
	}

	/**
	 * Returns the entry at the index in iteration order; a negative index counts from
	 * the end. Returns null if the map is null or the index is out of range.
	 */
	public static <K, V> Map.Entry<K, V> getEntry(Map<K, V> map, int index) {
		if (isEmpty(map)) {
			return null;
		}
		List<Map.Entry<K, V>> list = toEntries(map);
		return ListUtils.get(list, index);
	}

	/**
	 * Returns the first key mapped to the value, or null if not found or the map is null.
	 */
	public static <K, V> K getKey(Map<K, V> map, Object value) {
		if (map == null) {
			return null;
		}
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (value != null && value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Returns the value of the key converted to the required type, or the default
	 * value if the map is null, the value is absent or cannot be converted.
	 *
	 * <pre>
	 * MapUtils.get({i=5}, "i", Integer.class, 0)     = 5
	 * MapUtils.get({s="12"}, "s", Integer.class, 0)  = 12
	 * MapUtils.get({}, "missing", Integer.class, 7)  = 7
	 * </pre>
	 */
	public static <T> T get(Map<String, ?> map, String key, Class<T> requiredType, T defaultValue) {
		Object object = map != null ? map.get(key) : null;
		if (object == null) {
			return defaultValue;
		}
		T result;
		try {
			result = requiredType.cast(object);
		} catch (RuntimeException e) {
			result = ConvertUtils.convertValue(object, requiredType);
		}
		return result != null ? result : defaultValue;
	}

	/**
	 * Puts all entries into the map, ignoring a null collection.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void putAll(Map<K, V> map, Collection<Entry<K, V>> entries) {
		Assert.isNull(map, "Map must not be null.");
		if (CollectionUtils.isNotEmpty(entries)) {
			for (Entry<K, V> e : entries) {
				map.put(e.getKey(), e.getValue());
			}
		}
	}

	/**
	 * Puts all entries into the map, ignoring a null array.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void putAll(Map<K, V> map, Entry<K, V>[] entries) {
		Assert.isNull(map, "Map must not be null.");
		if (ArrayUtils.isNotEmpty(entries)) {
			for (Entry<K, V> e : entries) {
				map.put(e.getKey(), e.getValue());
			}
		}
	}

	/**
	 * Removes the first entry in iteration order, doing nothing if the map is null or empty.
	 */
	public static <K, V> void removeFirstEntry(Map<K, V> map) {
		if (isEmpty(map)) {
			return;
		}
		Map.Entry<K, V> firstEntry = getFirstEntry(map);
		map.remove(firstEntry.getKey());
	}

	/**
	 * Removes the last entry in iteration order, doing nothing if the map is null or empty.
	 */
	public static <K, V> void removeLastEntry(Map<K, V> map) {
		if (isEmpty(map)) {
			return;
		}
		Map.Entry<K, V> lastEntry = getLastEntry(map);
		map.remove(lastEntry.getKey());
	}

	/**
	 * Removes the given keys from the map.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void removeKeys(Map<K, V> map, Collection<?> keys) {
		Assert.isNull(map, "Map must not be null.");
		if (CollectionUtils.isNotEmpty(keys)) {
			for (Object key : keys) {
				map.remove(key);
			}
		}
	}

	/**
	 * Removes the given keys from the map.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void removeKeys(Map<K, V> map, Object[] keys) {
		Assert.isNull(map, "Map must not be null.");
		if (ArrayUtils.isNotEmpty(keys)) {
			for (Object key : keys) {
				map.remove(key);
			}
		}
	}

	/**
	 * Removes the entries whose keys are not in the collection. Nothing is removed if
	 * the collection is null or empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void retainKeys(Map<K, V> map, Collection<?> keys) {
		Assert.isNull(map, "Map must not be null.");
		if (CollectionUtils.isNotEmpty(keys)) {
			Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
			Object o;
			while (it.hasNext()) {
				o = it.next().getKey();
				if (!keys.contains(o)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Removes the entries whose keys are not in the array. Nothing is removed if the
	 * array is null or empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void retainKeys(Map<K, V> map, Object[] keys) {
		Assert.isNull(map, "Map must not be null.");
		if (ArrayUtils.isNotEmpty(keys)) {
			Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
			Object o;
			while (it.hasNext()) {
				o = it.next().getKey();
				if (ArrayUtils.notContains(keys, o)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Removes the entries whose values are not in the collection. Nothing is removed
	 * if the collection is null or empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void retainValues(Map<K, V> map, Collection<?> values) {
		Assert.isNull(map, "Map must not be null.");
		if (CollectionUtils.isNotEmpty(values)) {
			Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
			Object o;
			while (it.hasNext()) {
				o = it.next().getValue();
				if (!values.contains(o)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Removes the entries whose values are not in the array. Nothing is removed if
	 * the array is null or empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> void retainValues(Map<K, V> map, Object[] values) {
		Assert.isNull(map, "Map must not be null.");
		if (ArrayUtils.isNotEmpty(values)) {
			Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
			Object o;
			while (it.hasNext()) {
				o = it.next().getValue();
				if (ArrayUtils.notContains(values, o)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Zips two arrays into an ordered map; missing keys or values are null.
	 *
	 * <pre>
	 * MapUtils.toMap(["a", "b"], [1, 2]) = {a=1, b=2}
	 * MapUtils.toMap(["a", "b"], [1])    = {a=1, b=null}
	 * MapUtils.toMap(["a"], [1, 2])      = {a=1, null=2}
	 * </pre>
	 */
	public static <K, V> Map<K, V> toMap(K[] left, V[] right) {
		LinkedHashMap<K, V> results = new LinkedHashMap<K, V>();
		int leftLen = left != null ? left.length : 0;
		int rightLen = right != null ? right.length : 0;
		if (leftLen > rightLen) {
			int i = 0;
			for (; i < rightLen; i++) {
				results.put(left[i], right[i]);
			}
			for (; i < leftLen; i++) {
				results.put(left[i], null);
			}
		} else if (leftLen < rightLen) {
			int i = 0;
			for (; i < leftLen; i++) {
				results.put(left[i], right[i]);
			}
			for (; i < rightLen; i++) {
				results.put(null, right[i]);
			}
		} else {
			for (int i = 0; i < leftLen; i++) {
				results.put(left[i], right[i]);
			}
		}
		return results;
	}

	/**
	 * Returns a mutable ordered map holding the given pair.
	 */
	public static <K, V> Map<K, V> toMap(K key, V value) {
		Map<K, V> m = new LinkedHashMap<>();
		m.put(key, value);
		return m;
	}

	/**
	 * Returns a mutable ordered map holding the given pairs.
	 */
	public static <K, V> Map<K, V> toMap(K key, V value, K key2, V value2) {
		Map<K, V> m = toMap(key, value);
		m.put(key2, value2);
		return m;
	}

	/**
	 * Returns a mutable ordered map holding the given pairs.
	 */
	public static <K, V> Map<K, V> toMap(K key, V value, K key2, V value2, K key3, V value3) {
		Map<K, V> m = toMap(key, value, key2, value2);
		m.put(key3, value3);
		return m;
	}

	/**
	 * Pairs up the arguments as key1, value1, key2, value2 and so on. The value of a
	 * trailing key is null.
	 *
	 * <pre>
	 * MapUtils.toMap("a", "b", "c")                 = {a=b, c=null}
	 * MapUtils.toMap(null, "x", "y", null, "z")     = {null=x, y=null, z=null}
	 * MapUtils.toMap((Object[]) null)               = {}
	 * </pre>
	 */
	public static <T> Map<T, T> toMap(T... args) {
		LinkedHashMap<T, T> results = new LinkedHashMap<T, T>();
		if (args == null) {
			return results;
		}
		for (int i = 0; i < args.length; i += 2) {
			results.put(args[i], i + 1 < args.length ? args[i + 1] : null);
		}
		return results;
	}

	/**
	 * Pairs up the elements as key1, value1, key2, value2 and so on, returning an empty map for a null or empty collection.
	 */
	public static <T> Map<T, T> toMap(Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return emptyMap();
		}
		return toMap(c.iterator());
	}

	/**
	 * Pairs up the remaining elements as key1, value1, key2, value2 and so on, returning an empty map for null.
	 */
	public static <T> Map<T, T> toMap(Iterator<T> it) {
		if (it == null) {
			return emptyMap();
		}
		LinkedHashMap<T, T> results = new LinkedHashMap<T, T>();
		T key;
		while (it.hasNext()) {
			key = it.next();
			results.put(key, it.hasNext() ? it.next() : null);
		}
		return results;
	}

	/**
	 * Pairs up the remaining elements as key1, value1, key2, value2 and so on, returning an empty map for null.
	 */
	public static <T> Map<T, T> toMap(Enumeration<T> en) {
		if (en == null) {
			return emptyMap();
		}
		LinkedHashMap<T, T> results = new LinkedHashMap<T, T>();
		T key;
		while (en.hasMoreElements()) {
			key = en.nextElement();
			results.put(key, en.hasMoreElements() ? en.nextElement() : null);
		}
		return results;
	}

	/**
	 * Returns a new ordered map with the entries in reverse iteration order.
	 */
	public static <K, V> Map<K, V> reverse(Map<K, V> map) {
		if (isEmpty(map)) {
			return emptyMap();
		}
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.entrySet());
		Collections.reverse(entries);
		Map<K, V> results = new LinkedHashMap<K, V>();
		putAll(results, entries);
		return results;
	}

	/**
	 * Returns a new ordered map with keys and values swapped.
	 */
	public static <K, V> Map<V, K> exchange(Map<K, V> map) {
		if (isEmpty(map)) {
			return emptyMap();
		}
		Map<V, K> results = new LinkedHashMap<V, K>();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			results.put(entry.getValue(), entry.getKey());
		}
		return results;
	}

	/**
	 * Returns a new map containing the entries of both maps (dest wins on duplicated
	 * keys). If either argument is null, the other one is returned as it is.
	 *
	 * <pre>
	 * MapUtils.union({a=1, c=3}, {c=30, d=4}) = {a=1, c=30, d=4}
	 * MapUtils.union(null, null)              = null
	 * </pre>
	 */
	public static <K, V> Map<K, V> union(Map<K, V> src, Map<K, V> dest) {
		if (src == null && dest != null) {
			return dest;
		} else if (src != null && dest == null) {
			return src;
		} else if (src != null && dest != null) {
			Map<K, V> result = new LinkedHashMap<K, V>();
			result.putAll(src);
			result.putAll(dest);
			return result;
		}
		return null;
	}

	/**
	 * Returns the entries of src whose keys are not in dest. If either argument is
	 * null, the other one is returned as it is.
	 *
	 * <pre>
	 * MapUtils.minus({a=1, b=2, c=3}, {c=30, d=4}) = {a=1, b=2}
	 * MapUtils.minus(null, {c=30})                 = {c=30}
	 * </pre>
	 */
	public static <K, V> Map<K, V> minus(Map<K, V> src, Map<K, V> dest) {
		if (src == null && dest != null) {
			return dest;
		} else if (src != null && dest == null) {
			return src;
		} else if (src != null && dest != null) {
			Map<K, V> result = new LinkedHashMap<K, V>();
			for (Map.Entry<K, V> e : src.entrySet()) {
				if (!dest.containsKey(e.getKey())) {
					result.put(e.getKey(), e.getValue());
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * Returns the entries of src whose keys are also in dest, or null if either is null.
	 */
	public static <K, V> Map<K, V> intersect(Map<K, V> src, Map<K, V> dest) {
		if (src == null || dest == null) {
			return null;
		}
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> e : src.entrySet()) {
			if (dest.containsKey(e.getKey())) {
				result.put(e.getKey(), e.getValue());
			}
		}
		return result;
	}

	/**
	 * Joins the entries using "," both between keys and values and between entries.
	 */
	public static <K, V> String join(Map<K, V> map) {
		return join(map, ",");
	}

	/**
	 * Joins the entries using the delimiter both between keys and values and between entries.
	 */
	public static <K, V> String join(Map<K, V> map, String delimiter) {
		return join(map, delimiter, delimiter);
	}

	/**
	 * Joins the entries, returning "" for a null or empty map.
	 *
	 * <pre>
	 * MapUtils.join({a=1, b=2}, "=", ",") = "a=1,b=2"
	 * MapUtils.join(null, "=", ",")       = ""
	 * </pre>
	 */
	public static <K, V> String join(Map<K, V> map, String conjunction, String delimiter) {
		if (map == null) {
			return "";
		}
		Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
		if (!it.hasNext()) {
			return "";
		}
		if (conjunction == null) {
			conjunction = "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder("");
		Entry<K, V> e;
		K k;
		V v;
		while (true) {
			e = it.next();
			k = e.getKey();
			v = e.getValue();
			content.append(ObjectUtils.toString(k)).append(conjunction).append(ObjectUtils.toString(v));
			if (it.hasNext()) {
				content.append(delimiter);
			} else {
				break;
			}
		}
		return content.toString();
	}

	/**
	 * Returns the entries formatted like "{a=1,b=2}".
	 */
	public static String toString(Map map) {
		return "{" + join(map, "=", ",") + "}";
	}

	/**
	 * Checks if a map is null or empty.
	 *
	 * <pre>
	 * MapUtils.isEmpty(null)  = true
	 * MapUtils.isEmpty({})    = true
	 * MapUtils.isEmpty({a=1}) = false
	 * </pre>
	 */
	public static boolean isEmpty(Map map) {
		return map == null ? true : map.isEmpty();
	}

	/**
	 * Checks if a map is neither null nor empty.
	 */
	public static boolean isNotEmpty(Map map) {
		return !isEmpty(map);
	}

	/**
	 * Returns the entries of left whose values differ in right. Returns null if both
	 * are the same instance, and the other map if either is null.
	 */
	public static <K, V> Map<K, V> compareDifference(Map<K, V> left, Map<K, V> right) {
		if (left == right) {
			return null;
		}
		if (left == null && right != null) {
			return right;
		}
		if (left != null && right == null) {
			return left;
		}
		Map<K, V> result = new LinkedHashMap<K, V>();
		Iterator<Map.Entry<K, V>> it = left.entrySet().iterator();
		Map.Entry<K, V> e;
		K k;
		V v;
		for (; it.hasNext();) {
			e = it.next();
			k = e.getKey();
			v = e.getValue();
			if (ObjectUtils.notEquals(right.get(k), v)) {
				result.put(k, v);
			}
		}
		return result;
	}

	/**
	 * Compares two maps entry by entry (true if both are null).
	 */
	public static <K, V> boolean deepEquals(Map<K, V> left, Map<K, V> right) {
		if (left == right) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}
		int size = left.size();
		if (right.size() != size) {
			return false;
		}
		Iterator<Map.Entry<K, V>> it = left.entrySet().iterator();
		Map.Entry<K, V> e;
		K k;
		V v;
		for (; it.hasNext();) {
			e = it.next();
			k = e.getKey();
			v = e.getValue();
			if (ObjectUtils.notEquals(right.get(k), v)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Computes an iteration-order dependent hash code of the entries, or 0 for a null or empty map.
	 */
	public static <K, V> int deepHashCode(Map<K, V> map) {
		if (isEmpty(map)) {
			return 0;
		}
		int prime = 31;
		int result = 1;
		K key;
		V value;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			result = prime * result + ObjectUtils.hashCode(key);
			result = prime * result + ObjectUtils.hashCode(value);
		}
		return result;
	}

	/**
	 * Formats every value with {@link String#format(String, Object...)}.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> Map<K, String> formats(Map<K, V> map, String format) {
		Assert.isNull(map, "Map must not be null.");
		Map<K, String> results = new LinkedHashMap<K, String>();
		for (Map.Entry<K, V> en : map.entrySet()) {
			results.put(en.getKey(), String.format(format, en.getValue()));
		}
		return results;
	}

	private static class KeyComparator<K, V> implements Comparator<Map.Entry<K, V>> {

		@Override
		public int compare(Map.Entry<K, V> left, Map.Entry<K, V> right) {
			K leftKey = left.getKey();
			K rightKey = right.getKey();
			if (leftKey instanceof Comparable && rightKey instanceof Comparable) {
				return Comparables.compareTo((Comparable) leftKey, (Comparable) rightKey);
			}
			return 0;
		}

	}

	private static class ValueComparator<K, V> implements Comparator<Map.Entry<K, V>> {

		@Override
		public int compare(Map.Entry<K, V> left, Map.Entry<K, V> right) {
			V leftValue = left.getValue();
			V rightValue = right.getValue();
			if (leftValue instanceof Comparable && rightValue instanceof Comparable) {
				return Comparables.compareTo((Comparable) leftValue, (Comparable) rightValue);
			}
			return 0;
		}

	}

	/**
	 * Returns a comparator of entries by comparable keys; non-comparable keys are regarded as equal.
	 */
	public static <K, V> Comparator<Map.Entry<K, V>> keyComparator() {
		return new KeyComparator();
	}

	/**
	 * Returns a comparator of entries by comparable values; non-comparable values are regarded as equal.
	 */
	public static <K, V> Comparator<Map.Entry<K, V>> valueComparator() {
		return new ValueComparator();
	}

	/**
	 * Returns a new ordered map sorted by key in ascending order.
	 */
	public static <K, V> Map<K, V> sortByKey(Map<K, V> map) {
		return sortByKey(map, false);
	}

	/**
	 * Returns a new ordered map sorted by key.
	 */
	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, boolean reversed) {
		Comparator<Map.Entry<K, V>> c = keyComparator();
		if (reversed) {
			c = c.reversed();
		}
		return sort(map, c);
	}

	/**
	 * Returns a new ordered map sorted by value in ascending order.
	 */
	public static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		return sortByValue(map, false);
	}

	/**
	 * Returns a new ordered map sorted by value.
	 */
	public static <K, V> Map<K, V> sortByValue(Map<K, V> map, boolean reversed) {
		Comparator<Map.Entry<K, V>> c = valueComparator();
		if (reversed) {
			c = c.reversed();
		}
		return sort(map, c);
	}

	/**
	 * Returns a new ordered map sorted by the comparator, or the map itself if it is
	 * empty.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static <K, V> Map<K, V> sort(Map<K, V> map, Comparator<Map.Entry<K, V>> c) {
		Assert.isNull(map, "Map must not be null.");
		if (map.size() > 0) {
			return map.entrySet().stream().sorted(c)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		}
		return map;
	}

	/**
	 * Merges right into left and puts the result into output (left and right are not
	 * modified).
	 *
	 * @return the previous values of left which were overridden by right
	 */
	public static Map<String, String> mergeProperties(Properties left, Properties right, Map<String, String> output) {
		Map<String, String> t = new LinkedHashMap<String, String>(toMap(left));
		Map<String, String> prevs = new HashMap<String, String>();
		String name, prev;
		for (Enumeration<?> en = right != null ? right.propertyNames() : emptyEnumeration(); en.hasMoreElements();) {
			name = en.nextElement().toString();
			prev = t.put(name, right.getProperty(name));
			if (prev != null) {
				prevs.put(name, prev);
			}
		}
		output.putAll(t);
		return prevs;
	}

	/**
	 * Copies the properties (including defaults) into a new ordered map, or returns an empty map for null.
	 */
	public static Map<String, String> toMap(Properties src) {
		if (isEmpty(src)) {
			return Collections.EMPTY_MAP;
		}
		Map<String, String> result = new LinkedHashMap<String, String>();
		Enumeration<?> en = src.propertyNames();
		String name, value;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			result.put(name, src.getProperty(name));
		}
		return result;
	}

	/**
	 * Copies the properties whose names start with "prefix." into a new map, with the
	 * prefix and the separator stripped from the names.
	 *
	 * <pre>
	 * MapUtils.toMap({db.url=jdbc, db=root, other=x}, "db") = {url=jdbc}
	 * </pre>
	 */
	public static Map<String, String> toMap(Properties src, String prefix) {
		Map<String, String> dest = new LinkedHashMap<String, String>();
		copyProperties(src, prefix, dest);
		return dest;
	}

	/**
	 * Copies the properties whose names match the substring into a new map.
	 */
	public static Map<String, String> toMap(Properties src, String substr, MatchMode matchMode) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		copyProperties(src, substr, matchMode, result);
		return result;
	}

	/**
	 * Copies the map into a new Properties, skipping null keys and values.
	 *
	 * @throws IllegalArgumentException if the map is null
	 */
	public static Properties toProperties(Map<String, String> map) {
		Assert.isNull(map, "Map must not be null.");
		Properties prop = new Properties();
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (e.getKey() != null && e.getValue() != null) {
				prop.setProperty(e.getKey(), e.getValue());
			}
		}
		return prop;
	}

	/**
	 * Copies all properties (including defaults) of src into dest.
	 *
	 * @throws IllegalArgumentException if src is null
	 */
	public static void copyProperties(Properties src, Properties dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			dest.setProperty(name, src.getProperty(name));
		}
	}

	/**
	 * Copies the properties whose names start with "prefix." into dest, with the
	 * prefix and the separator stripped from the names.
	 *
	 * @throws IllegalArgumentException if src is null
	 */
	public static void copyProperties(Properties src, String prefix, Properties dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			if (name.startsWith(prefix)) {
				if (name.length() > prefix.length()) {
					dest.setProperty(name.substring(prefix.length() + 1), src.getProperty(name));
				}
			}
		}
	}

	/**
	 * Copies the properties whose names start with "prefix." into dest, with the
	 * prefix and the separator stripped from the names.
	 *
	 * @throws IllegalArgumentException if src is null
	 */
	public static void copyProperties(Properties src, String prefix, Map<String, String> dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			if (name.startsWith(prefix)) {
				if (name.length() > prefix.length()) {
					dest.put(name.substring(prefix.length() + 1), src.getProperty(name));
				}
			}
		}
	}

	/**
	 * Copies the properties whose names match the substring into dest. Nothing is
	 * copied if the match mode is null.
	 *
	 * @throws IllegalArgumentException if src is null
	 */
	public static void copyProperties(Properties src, String substr, MatchMode matchMode, Properties dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name, value;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			if (matchMode != null && matchMode.matches(name, substr)) {
				value = src.getProperty(name);
				dest.setProperty(name, value);
			}
		}
	}

	/**
	 * Copies the properties whose names match the substring into dest. Nothing is
	 * copied if the match mode is null.
	 *
	 * @throws IllegalArgumentException if src is null
	 */
	public static void copyProperties(Properties src, String substr, MatchMode matchMode, Map<String, String> dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name, value;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			if (matchMode != null && matchMode.matches(name, substr)) {
				value = src.getProperty(name);
				dest.put(name, value);
			}
		}
	}

	/**
	 * Returns the entries whose values match the substring.
	 */
	public static <K> Map<K, String> toMatchedValueMap(Map<K, String> m, String substr, MatchMode matchMode) {
		Map<K, String> result = new LinkedHashMap<K, String>();
		copyValues(m, substr, matchMode, result);
		return result;
	}

	/**
	 * Returns the entries whose keys match the substring.
	 */
	public static <V> Map<String, V> toMatchedKeyMap(Map<String, V> m, String substr, MatchMode matchMode) {
		Map<String, V> result = new LinkedHashMap<String, V>();
		copyKeys(m, substr, matchMode, result);
		return result;
	}

	/**
	 * Copies the entries whose keys match the substring into target.
	 *
	 * @throws IllegalArgumentException if the source map is null
	 */
	public static <V> void copyKeys(Map<String, V> map, String substr, MatchMode matchMode, Map<String, V> target) {
		Assert.isNull(map, "Source map must not be null.");
		String key;
		for (Map.Entry<String, V> entry : map.entrySet()) {
			key = entry.getKey();
			if (matchMode != null && matchMode.matches(key, substr)) {
				target.put(key, entry.getValue());
			}
		}
	}

	/**
	 * Copies the entries whose values match the substring into target.
	 *
	 * @throws IllegalArgumentException if the source map is null
	 */
	public static <K> void copyValues(Map<K, String> map, String substr, MatchMode matchMode, Map<K, String> target) {
		Assert.isNull(map, "Source map must not be null.");
		String value;
		for (Map.Entry<K, String> entry : map.entrySet()) {
			value = entry.getValue();
			if (matchMode != null && matchMode.matches(value, substr)) {
				target.put(entry.getKey(), value);
			}
		}
	}

	/**
	 * Pairs up the arguments as string keys and values.
	 *
	 * <pre>
	 * MapUtils.populate("a", 1, "b") = {a=1, b=null}
	 * </pre>
	 *
	 * @throws IllegalArgumentException if kwargs is null
	 */
	public static Map<String, String> populate(Object... kwargs) {
		Assert.isNull(kwargs);
		Map<String, String> map = new LinkedHashMap<String, String>();
		int l = kwargs.length;
		int i = 0;
		for (; i < l; i++) {
			if ((i & 1) == 1) {
				map.put(ObjectUtils.toString(kwargs[i - 1]), ObjectUtils.toString(kwargs[i]));
			}
		}
		if ((i & 1) == 1) {
			map.put(ObjectUtils.toString(kwargs[i - 1]), null);
		}
		return map;
	}

	/**
	 * Keeps the first value of every array, returning an empty map for a null or empty map.
	 */
	public static <K, V> Map<K, V> toSingleValueMap(Map<K, V[]> multiValueMap) {
		return toSingleValueMap(multiValueMap, true);
	}

	/**
	 * Keeps the first (or last) value of every array, returning an empty map for a null or empty map.
	 */
	public static <K, V> Map<K, V> toSingleValueMap(Map<K, V[]> multiValueMap, boolean firstValue) {
		if (MapUtils.isEmpty(multiValueMap)) {
			return emptyMap();
		}
		Map<K, V> data = new LinkedHashMap<K, V>();
		V value;
		for (Map.Entry<K, V[]> entry : multiValueMap.entrySet()) {
			value = firstValue ? (V) ArrayUtils.getFirst(entry.getValue()) : (V) ArrayUtils.getLast(entry.getValue());
			data.put(entry.getKey(), value);
		}
		return data;
	}

	private static Enumeration<?> emptyEnumeration() {
		return Collections.emptyEnumeration();
	}

	private static class SingletonEntry implements Map.Entry {

		private SingletonEntry(Object key, Object value) {
			Assert.isNull(key, "Key must not be null.");
			this.key = key;
			this.value = value;
		}

		private Object key;
		private Object value;

		@Override
		public Object getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
			return ObjectUtils.equals(key, other.getKey()) && ObjectUtils.equals(value, other.getValue());
		}

		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}
}
