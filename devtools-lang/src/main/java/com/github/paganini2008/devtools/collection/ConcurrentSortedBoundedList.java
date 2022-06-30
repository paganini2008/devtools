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
		return result && ensureCapacity(e);
	}

	@Override
	public E set(int index, E element) {
		E previous = delegate.set(index, element);
		if (previous != null) {
			keys.remove(previous);
		}
		ensureCapacity(element);
		return previous;
	}

	@Override
	public void add(int index, E element) {
		delegate.add(index, element);
		ensureCapacity(element);
	}

	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		if (delegate.remove(o)) {
			keys.remove(o);
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (delegate.removeAll(c)) {
			keys.removeAll(c);
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (delegate.retainAll(c)) {
			keys.retainAll(c);
			return true;
		}
		return false;
	}

	@Override
	public E remove(int index) {
		E previous = delegate.remove(index);
		keys.remove(previous);
		return previous;
	}

	@Override
	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return delegate.subList(fromIndex, toIndex);
	}

	@Override
	public E get(int index) {
		return delegate.get(index);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Collection<E> getDelegate() {
		return delegate;
	}

	private boolean ensureCapacity(E e) {
		boolean reached = false;
		E eldestElement = null;
		synchronized (keys) {
			boolean a = keys.add(e);
			if (a) {
				if (reached = (keys.size() > maxSize)) {
					eldestElement = asc ? keys.pollFirst() : keys.pollLast();
					delegate.remove(eldestElement);
				}
			} else {
				delegate.remove(e);
			}
		}
		if (reached) {
			onEviction(eldestElement);
		}
		return reached;
	}

}
