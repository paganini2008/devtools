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
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class MapUtils {

	public static <K, V> Map<K, V> emptyMap() {
		return Collections.EMPTY_MAP;
	}

	public static <K, V> Map.Entry singletonEntry(K key, V value) {
		return new SingletonEntry(key, value);
	}

	public static <K, V> Map<K, V> synchronizedHashMap(int initialCapacity, float loadFactor) {
		return Collections.synchronizedMap(new HashMap<>(initialCapacity, loadFactor));
	}

	public static <K, V> Map<K, V> synchronizedHashMap(int initialCapacity) {
		return Collections.synchronizedMap(new HashMap<>(initialCapacity, 0.75F));
	}

	public static <K, V> Map<K, V> synchronizedHashMap() {
		return Collections.synchronizedMap(new HashMap<>());
	}

	public static <K, V> Map<K, V> synchronizedHashMap(Map<K, V> m) {
		return Collections.synchronizedMap(new HashMap<>(m));
	}

	public static <K, V> Map<K, V> synchronizedLinkedHashMap() {
		return Collections.synchronizedMap(new LinkedHashMap<>());
	}

	public static <K, V> Map<K, V> synchronizedLinkedHashMap(Map<K, V> m) {
		return Collections.synchronizedMap(new LinkedHashMap<>(m));
	}

	public static <K, V> Map<K, V> synchronizedLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		return Collections.synchronizedMap(new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder));
	}

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

	public static <K, V> Map<K, V> singletonMap(Map<K, V> map) {
		return singletonMap(map, true);
	}

	public static <K, V> Map<K, V> singletonMap(Map<K, V> map, boolean firstEntry) {
		Map.Entry<K, V> entry = firstEntry ? getFirstEntry(map) : getLastEntry(map);
		return singletonMap(entry);
	}

	public static <K, V> Map<K, V> singletonMap(Map.Entry<K, V> entry) {
		Assert.isNull(entry);
		return Collections.singletonMap(entry.getKey(), entry.getValue());
	}

	public static boolean isMap(Object obj) {
		return obj == null ? false : obj instanceof Map;
	}

	public static boolean isNotMap(Object obj) {
		return !isMap(obj);
	}

	public static Object getIfRequired(Map<String, Object> map, String key) {
		return getIfRequired(map, key, new MissingKeyException(key));
	}

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

	public static <K, V> V get(Map<K, V> map, K key) {
		return getOrDefault(map, key, (V) null);
	}

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

	public static <K, V> V get(Map<K, V> map, K key, Supplier<V> supplier) {
		if (map != null) {
			V value = map.get(key);
			if (value == null) {
				synchronized (supplier) {
					value = map.get(key);
					if (value == null) {
						map.putIfAbsent(key, supplier.get());
					}
				}
				value = map.get(key);
			}
			return value;
		}
		return null;
	}

	public static <K, V> V get(Map<K, V> map, K key, Function<K, V> function) {
		if (map != null) {
			V value = map.get(key);
			if (value == null) {
				synchronized (function) {
					value = map.get(key);
					if (value == null) {
						map.putIfAbsent(key, function.apply(key));
					}
				}
				value = map.get(key);
			}
			return value;
		}
		return null;
	}

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

	public static <K, V> List<Map.Entry<K, V>> toEntries(Map<K, V> map) {
		if (isEmpty(map)) {
			return ListUtils.emptyList();
		}
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}

	public static <K, V> Entry<K, V> getFirstEntry(Map<K, V> map) {
		Assert.isNull(map, "Map must not be null.");
		return CollectionUtils.getFirst(map.entrySet().iterator());
	}

	public static <K, V> Entry<K, V> getLastEntry(Map<K, V> map) {
		Assert.isNull(map, "Map must not be null.");
		return CollectionUtils.getLast(map.entrySet().iterator());
	}

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a12", "120");
		map.put("a", "123");
		map.put("a1", "923");
		map.put("a9", "123");
		map.put("b", "234");
		map.put("c", "345");
		System.out.println(sortByValue(map, true));
	}

	public static <K, V> Map.Entry<K, V> getEntry(Map<K, V> map, int index) {
		if (isEmpty(map)) {
			return null;
		}
		List<Map.Entry<K, V>> list = toEntries(map);
		return ListUtils.get(list, index);
	}

	public static <K, V> K getKey(Map<K, V> map, Object value) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (value != null && value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static <T> T get(Map<String, ?> map, String key, Class<T> requiredType, T defaultValue) {
		Object object = map.get(key);
		try {
			return requiredType.cast(object);
		} catch (RuntimeException e) {
			return ConvertUtils.convertValue(object, requiredType);
		}
	}

	public static <K, V> void putAll(Map<K, V> map, Collection<Entry<K, V>> entries) {
		Assert.isNull(map, "Map must not be null.");
		if (CollectionUtils.isNotEmpty(entries)) {
			for (Entry<K, V> e : entries) {
				map.put(e.getKey(), e.getValue());
			}
		}
	}

	public static <K, V> void putAll(Map<K, V> map, Entry<K, V>[] entries) {
		Assert.isNull(map, "Map must not be null.");
		if (ArrayUtils.isNotEmpty(entries)) {
			for (Entry<K, V> e : entries) {
				map.put(e.getKey(), e.getValue());
			}
		}
	}

	public static <K, V> void removeFirstEntry(Map<K, V> map) {
		Map.Entry<K, V> firstEntry = getFirstEntry(map);
		map.remove(firstEntry.getKey());
	}

	public static <K, V> void removeLastEntry(Map<K, V> map) {
		Map.Entry<K, V> lastEntry = getLastEntry(map);
		map.remove(lastEntry.getKey());
	}

	public static <K, V> void removeKeys(Map<K, V> map, Collection<?> keys) {
		Assert.isNull(map, "Map must not be null.");
		if (CollectionUtils.isNotEmpty(keys)) {
			for (Object key : keys) {
				map.remove(key);
			}
		}
	}

	public static <K, V> void removeKeys(Map<K, V> map, Object[] keys) {
		Assert.isNull(map, "Map must not be null.");
		if (ArrayUtils.isNotEmpty(keys)) {
			for (Object key : keys) {
				map.remove(key);
			}
		}
	}

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

	public static <K, V> Map<K, V> toMap(K key, V value) {
		Map<K, V> m = new LinkedHashMap<>();
		m.put(key, value);
		return m;
	}

	public static <K, V> Map<K, V> toMap(K key, V value, K key2, V value2) {
		Map<K, V> m = toMap(key, value);
		m.put(key2, value2);
		return m;
	}

	public static <K, V> Map<K, V> toMap(K key, V value, K key2, V value2, K key3, V value3) {
		Map<K, V> m = toMap(key, value, key2, value2);
		m.put(key3, value3);
		return m;
	}

	public static <T> Map<T, T> toMap(T... args) {
		LinkedHashMap<T, T> results = new LinkedHashMap<T, T>();
		T prev = null;
		for (T arg : args) {
			if (prev == null) {
				prev = arg;
			} else {
				results.put(prev, arg);
				prev = null;
			}
		}
		if (prev != null) {
			results.put(prev, null);
		}
		return results;
	}

	public static <T> Map<T, T> toMap(Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return emptyMap();
		}
		return toMap(c.iterator());
	}

	public static <T> Map<T, T> toMap(Iterator<T> it) {
		if (it == null) {
			return emptyMap();
		}
		LinkedHashMap<T, T> results = new LinkedHashMap<T, T>();
		T prev = null, item = null;
		while (it.hasNext()) {
			item = it.next();
			if (prev == null) {
				prev = item;
			} else {
				results.put(prev, item);
				prev = null;
			}
		}
		if (prev != null) {
			results.put(prev, null);
		}
		return results;
	}

	public static <T> Map<T, T> toMap(Enumeration<T> en) {
		if (en == null) {
			return emptyMap();
		}
		LinkedHashMap<T, T> results = new LinkedHashMap<T, T>();
		T prev = null, item = null;
		while (en.hasMoreElements()) {
			item = en.nextElement();
			if (prev == null) {
				prev = item;
			} else {
				results.put(prev, item);
				prev = null;
			}
		}
		if (prev != null) {
			results.put(prev, null);
		}
		return results;
	}

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

	public static <K, V> String join(Map<K, V> map) {
		return join(map, ",");
	}

	public static <K, V> String join(Map<K, V> map, String delimiter) {
		return join(map, delimiter, delimiter);
	}

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

	public static String toString(Map map) {
		return "{" + join(map, "=", ",") + "}";
	}

	public static boolean isEmpty(Map map) {
		return map == null ? true : map.isEmpty();
	}

	public static boolean isNotEmpty(Map map) {
		return !isEmpty(map);
	}

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

	public static <K, V> Comparator<Map.Entry<K, V>> keyComparator() {
		return new KeyComparator();
	}

	public static <K, V> Comparator<Map.Entry<K, V>> valueComparator() {
		return new ValueComparator();
	}

	public static <K, V> Map<K, V> sortByKey(Map<K, V> map) {
		return sortByKey(map, false);
	}

	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, boolean reversed) {
		Comparator<Map.Entry<K, V>> c = keyComparator();
		if (reversed) {
			c = c.reversed();
		}
		return sort(map, c);
	}

	public static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		return sortByValue(map, false);
	}

	public static <K, V> Map<K, V> sortByValue(Map<K, V> map, boolean reversed) {
		Comparator<Map.Entry<K, V>> c = valueComparator();
		if (reversed) {
			c = c.reversed();
		}
		return sort(map, c);
	}

	public static <K, V> Map<K, V> sort(Map<K, V> map, Comparator<Map.Entry<K, V>> c) {
		Assert.isNull(map, "Map must not be null.");
		if (map.size() > 0) {
			return map.entrySet().stream().sorted(c)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		}
		return map;
	}

	public static Map<String, String> mergeProperties(Properties left, Properties right, Map<String, String> output) {
		Map<String, String> t = toMap(left);
		Map<String, String> prevs = new HashMap<String, String>();
		String name, prev;
		for (Enumeration<?> en = right.propertyNames(); en.hasMoreElements();) {
			name = en.nextElement().toString();
			prev = t.put(name, right.getProperty(name));
			if (prev != null) {
				prevs.put(name, prev);
			}
		}
		output.putAll(t);
		return prevs;
	}

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

	public static Map<String, String> toMap(Properties src, String prefix) {
		Map<String, String> dest = new LinkedHashMap<String, String>();
		copyProperties(src, prefix, dest);
		return dest;
	}

	public static Map<String, String> toMap(Properties src, String substr, MatchMode matchMode) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		copyProperties(src, substr, matchMode, result);
		return result;
	}

	public static Properties toProperties(Map<String, String> map) {
		Assert.isNull(map, "Map must not be null.");
		Properties prop = new Properties();
		for (Map.Entry<String, String> e : map.entrySet()) {
			prop.setProperty(e.getKey(), e.getValue());
		}
		return prop;
	}

	public static void copyProperties(Properties src, Properties dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			dest.setProperty(name, src.getProperty(name));
		}
	}

	public static void copyProperties(Properties src, String prefix, Properties dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			if (name.startsWith(prefix)) {
				dest.setProperty(name.substring(prefix.length() + 1), src.getProperty(name));
			}
		}
	}

	public static void copyProperties(Properties src, String prefix, Map<String, String> dest) {
		Assert.isNull(src, "Properties must not be null.");
		Enumeration<?> en = src.propertyNames();
		String name;
		while (en.hasMoreElements()) {
			name = (String) en.nextElement();
			if (name.startsWith(prefix)) {
				dest.put(name.substring(prefix.length() + 1), src.getProperty(name));
			}
		}
	}

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

	public static <K> Map<K, String> toMatchedValueMap(Map<K, String> m, String substr, MatchMode matchMode) {
		Map<K, String> result = new LinkedHashMap<K, String>();
		copyValues(m, substr, matchMode, result);
		return result;
	}

	public static <V> Map<String, V> toMatchedKeyMap(Map<String, V> m, String substr, MatchMode matchMode) {
		Map<String, V> result = new LinkedHashMap<String, V>();
		copyKeys(m, substr, matchMode, result);
		return result;
	}

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

	public static <K, V> Map<K, V> toSingleValueMap(Map<K, V[]> multiValueMap) {
		return toSingleValueMap(multiValueMap, true);
	}

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
	}

}
