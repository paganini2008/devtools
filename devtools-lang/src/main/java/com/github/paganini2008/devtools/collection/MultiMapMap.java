package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * MultiMapMap
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MultiMapMap<K, N, V> extends AbstractMap<K, Map<N, V>> implements Map<K, Map<N, V>>, Serializable {

	private static final long serialVersionUID = 1256299819433654455L;

	private final Map<K, Map<N, V>> delegate;

	public MultiMapMap() {
		this(new LinkedHashMap<K, Map<N, V>>());
	}

	protected MultiMapMap(Map<K, Map<N, V>> delegate) {
		this.delegate = delegate;
	}

	public V get(K key, N name) {
		return get(key, name, null);
	}

	public V get(K key, N name, V defaultValue) {
		Map<N, V> map = delegate.get(key);
		V v = null;
		if (map != null) {
			v = map.get(name);
		}
		if (v == null) {
			v = defaultValue;
		}
		return v;
	}

	public Map<N, V> get(Object key) {
		return delegate.get(key);
	}

	public Map<N, V> remove(Object key) {
		return delegate.remove(key);
	}

	public V removeValue(K key, N name) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			return map.remove(name);
		}
		return null;
	}

	public void clear() {
		delegate.clear();
	}

	public void clear(K key) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			map.clear();
		}
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public boolean containsValue(K key, N name) {
		Map<N, V> map = delegate.get(key);
		if (map != null) {
			return map.containsKey(name);
		}
		return false;
	}

	public void append(K key, Map<N, V> value) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, createValueMap());
			map = delegate.get(key);
		}
		map.putAll(value);
	}

	public Map<N, V> put(K key, Map<N, V> value) {
		return delegate.put(key, value);
	}

	public V put(K key, N name, V value) {
		Map<N, V> map = delegate.get(key);
		if (map == null) {
			delegate.putIfAbsent(key, createValueMap());
			map = delegate.get(key);
		}
		return map.put(name, value);
	}

	protected Map<N, V> createValueMap() {
		return new LinkedHashMap<N, V>();
	}

	public int size() {
		return delegate.size();
	}

	public int size(K key) {
		Map<N, V> map = delegate.get(key);
		return map != null ? map.size() : 0;
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public Set<Map.Entry<K, Map<N, V>>> entrySet() {
		return delegate.entrySet();
	}

	public String toString() {
		return delegate.toString();
	}

	public static <K, N, V> Map<K, Map<N, V>> synchronizedMap() {
		return new MultiMapMap<K, N, V>(new ConcurrentHashMap<K, Map<N, V>>()) {

			private static final long serialVersionUID = 1L;

			protected Map<N, V> createValueMap() {
				return new ConcurrentHashMap<N, V>();
			}

		};
	}

	public static void main(String[] args) {
		MultiMapMap<String, String, String> map = new MultiMapMap<>();
		for (int i = 0; i < 100; i++) {
			map.put("Key_" + i, "Name_" + i, UUID.randomUUID().toString());
		}
		System.out.println(map);
	}
}