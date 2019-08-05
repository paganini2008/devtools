package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LruList
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LruList<E> extends AbstractList<E> implements List<E>, Serializable {

	private static final long serialVersionUID = -216068975490011223L;

	public LruList() {
		this(128);
	}

	public LruList(int maxSize) {
		this(new CopyOnWriteArrayList<E>(), maxSize);
	}

	public LruList(final List<E> delegate, final int maxSize) {
		this.delegate = delegate;
		this.keys = Collections.synchronizedMap(new LinkedHashMap<Integer, E>(16, 0.75F, true) {

			private static final long serialVersionUID = -3128504588768026293L;

			protected boolean removeEldestEntry(Map.Entry<Integer, E> eldest) {
				boolean result = size() > maxSize;
				if (result) {
					delegate.remove(eldest.getValue());
					onEviction(eldest.getValue());
				}
				return result;
			}
		});
	}

	private final List<E> delegate;
	private final AtomicInteger index = new AtomicInteger(0);
	private final Map<Integer, E> keys;

	public boolean add(E e) {
		keys.put(index.getAndIncrement(), e);
		return delegate.add(e);
	}

	public boolean contains(Object o) {
		keys.get(delegate.indexOf(o));
		return delegate.contains(o);
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	public boolean remove(Object o) {
		keys.remove(delegate.indexOf(o));
		return delegate.remove(o);
	}

	public int size() {
		return delegate.size();
	}

	public E get(int index) {
		keys.get(index);
		return delegate.get(index);
	}

	public void add(int index, E e) {
		keys.put(index, e);
		delegate.add(index, e);
	}

	public E set(int index, E e) {
		keys.put(index, e);
		return delegate.set(index, e);
	}

	public ListIterator<E> listIterator(int index) {
		return delegate.listIterator(index);
	}

	public String toString() {
		return delegate.toString();
	}

	protected void onEviction(E e) {
	}

}
