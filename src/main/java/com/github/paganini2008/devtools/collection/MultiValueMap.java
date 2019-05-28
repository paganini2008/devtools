package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * MultiValueMap
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MultiValueMap<K, V> extends AbstractMap<K, List<V>> implements Map<K, List<V>>, Serializable {

	private static final long serialVersionUID = 4293668328277273376L;
	private final Map<K, List<V>> delegate;

	public MultiValueMap() {
		this(new HashMap<K, List<V>>());
	}

	protected MultiValueMap(Map<K, List<V>> delegate) {
		this.delegate = delegate;
	}

	public void clear() {
		delegate.clear();
	}

	public void clear(K key) {
		List<V> list = delegate.get(key);
		if (list != null) {
			list.clear();
		}
	}

	public List<V> remove(Object key) {
		return delegate.remove(key);
	}

	public V remove(K key, int index) {
		List<V> list = delegate.get(key);
		return ListUtils.remove(list, index);
	}

	public V removeLast(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.removeLast(list);
	}

	public V removeFirst(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.removeFirst(list);
	}

	public V getFirst(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.getFirst(list);
	}

	public V getLast(K key) {
		List<V> list = delegate.get(key);
		return ListUtils.getLast(list);
	}

	public V get(K key, int index) {
		List<V> list = delegate.get(key);
		return ListUtils.get(list, index);
	}

	public List<V> get(Object key) {
		return delegate.get(key);
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public void removeValue(K key, V value) {
		List<V> list = delegate.get(key);
		if (list != null) {
			while (list.contains(value)) {
				list.remove(value);
			}
		}
	}

	public Set<Map.Entry<K, List<V>>> entrySet() {
		return delegate.entrySet();
	}

	public boolean containsValue(K key, V value) {
		List<V> list = delegate.get(key);
		return list != null ? list.contains(value) : false;
	}

	public int size() {
		return delegate.size();
	}

	public Set<K> keys() {
		return delegate.keySet();
	}

	public List<V> put(K key, List<V> value) {
		return delegate.put(key, value);
	}

	public void addAll(K key, Collection<V> values) {
		List<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, createValueList());
			list = delegate.get(key);
		}
		list.addAll(values);
	}

	public V add(K key, V value) {
		List<V> list = delegate.get(key);
		if (list == null) {
			delegate.putIfAbsent(key, createValueList());
			list = delegate.get(key);
		}
		list.add(value);
		return value;
	}

	protected List<V> createValueList() {
		return new ArrayList<V>();
	}

	public Map<K, V> toSingleValueMap() {
		final Map<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, List<V>> entry : delegate.entrySet()) {
			map.put(entry.getKey(), ListUtils.getFirst(entry.getValue()));
		}
		return map;
	}

	public static <K, V> Map<K, List<V>> synchronizedMap() {
		return new MultiValueMap<K, V>(new ConcurrentHashMap<K, List<V>>()) {

			private static final long serialVersionUID = 1L;

			protected List<V> createValueList() {
				return new CopyOnWriteArrayList<V>();
			}
		};
	}

	public String toString() {
		return delegate.toString();
	}

}
