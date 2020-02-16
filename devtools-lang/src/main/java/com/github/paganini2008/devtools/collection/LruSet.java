package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * LruSet
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class LruSet<E> extends AbstractSet<E> implements Set<E>, Serializable {

	private static final long serialVersionUID = 1472051002956521109L;

	public LruSet() {
		this(128);
	}

	public LruSet(int maxSize) {
		this(new CopyOnWriteArraySet<E>(), maxSize);
	}

	public LruSet(final Set<E> delegate, final int maxSize) {
		this.delegate = delegate;
		this.keys = Collections.synchronizedMap(new LinkedHashMap<E, Object>(16, 0.75F, true) {

			private static final long serialVersionUID = -1535588414267472317L;

			protected boolean removeEldestEntry(Map.Entry<E, Object> eldest) {
				boolean result = size() > maxSize;
				if (result) {
					E eldestValue = eldest.getKey();
					delegate.remove(eldestValue);
					onEviction(eldestValue);
				}
				return result;
			}
		});
	}

	private final Set<E> delegate;
	private final Map<E, Object> keys;

	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	public int size() {
		return delegate.size();
	}

	public boolean contains(Object o) {
		keys.get(o);
		return delegate.contains(o);
	}

	public boolean add(E e) {
		keys.put(e, e);
		return delegate.add(e);
	}

	public boolean remove(Object o) {
		keys.remove(o);
		return delegate.remove(o);
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public String toString() {
		return delegate.toString();
	}

	protected void onEviction(E e) {
	}

}
