package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * ConcurrentSortedBoundedList
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class ConcurrentSortedBoundedList<E> extends AbstractList<E> implements Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -4261600807536071807L;
	private final List<E> delegate;
	private final int maxSize;
	private final NavigableSet<E> keys;

	public ConcurrentSortedBoundedList(int maxSize) {
		this(new CopyOnWriteArrayList<E>(), maxSize);
	}

	public ConcurrentSortedBoundedList(List<E> delegate, int maxSize) {
		this(delegate, maxSize, new ConcurrentSkipListSet<E>());
	}

	protected ConcurrentSortedBoundedList(List<E> delegate, int maxSize, NavigableSet<E> keys) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = keys;
	}

	private boolean asc = true;

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	@Override
	public boolean add(E e) {
		boolean result = delegate.add(e);
		return true;
	}

	@Override
	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return super.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		// TODO Auto-generated method stub
		super.add(index, element);
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return super.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return super.lastIndexOf(o);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return super.subList(fromIndex, toIndex);
	}

	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<E> getDelegate() {
		return delegate;
	}

	private void ensureCapacity(E e) {
		boolean reached;
		E eldestElement = null;
		synchronized (keys) {
			keys.add(e);
			if (reached = (keys.size() > maxSize)) {
				eldestElement = asc ? keys.pollFirst() : keys.pollLast();
				delegate.remove(eldestElement);
			}
		}
		if (reached) {
			onEviction(eldestElement);
		}
	}

}
