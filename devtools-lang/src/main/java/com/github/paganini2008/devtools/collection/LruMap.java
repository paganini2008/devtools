package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LruMap
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 * @see LinkedHashMap
 */
public class LruMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable{

	private static final long serialVersionUID = -1958272189245104075L;

	public LruMap() {
		this(128);
	}

	public LruMap(int maxSize) {
		this(new ConcurrentHashMap<K, V>(), maxSize);
	}

	public LruMap(final Map<K, V> delegate, final int maxSize) {
		this.delegate = delegate;
		this.keys = Collections.synchronizedMap(new LinkedHashMap<K, Object>(16, 0.75F, true) {

			private static final long serialVersionUID = -8320557662210764484L;

			protected boolean removeEldestEntry(Map.Entry<K, Object> eldest) {
				boolean result;
				if (result = size() > maxSize) {
					V eldestValue = delegate.remove(eldest.getKey());
					onEviction(eldest.getKey(), eldestValue);
				}
				return result;
			}
		});
	}

	private final Map<K, V> delegate;
	private final Map<K, Object> keys;

	public V get(Object key) {
		keys.get(key);
		return delegate.get(key);
	}

	public V put(K key, V value) {
		keys.put(key, key);
		return delegate.put(key, value);
	}

	public V remove(Object key) {
		keys.remove(key);
		return delegate.remove(key);
	}

	public int size() {
		return delegate.size();
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	public boolean containsKey(Object key) {
		keys.get(key);
		return delegate.containsKey(key);
	}

	public Set<K> keySet() {
		return delegate.keySet();
	}

	public Collection<V> values() {
		return delegate.values();
	}

	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	public String toString() {
		return delegate.toString();
	}

	protected void onEviction(K key, V eldestValue) {
	}

}
