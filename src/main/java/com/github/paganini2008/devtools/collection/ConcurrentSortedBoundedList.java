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
 * <p>
 * A thread-safe list of distinct elements bounded by a maximum size which,
 * when the size is exceeded, evicts the smallest element (or the largest one if
 * {@link #setAsc(boolean)} is set to false). Elements must be mutually
 * comparable. Adding an element which is already present moves it to the end
 * of the list instead of duplicating it.
 *
 * @author Fred Feng
 * @date 2021-12-16
 *
 * @since 2.0.4
 */
public class ConcurrentSortedBoundedList<E> extends AbstractList<E> implements Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -4261600807536071807L;
	private final List<E> delegate;
	private final int maxSize;
	private final NavigableSet<E> keys;

	/**
	 * Creates a ConcurrentSortedBoundedList backed by a
	 * {@link CopyOnWriteArrayList}.
	 * 
	 * @param maxSize
	 *            the maximum number of elements
	 */
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

	private volatile boolean asc = true;

	/**
	 * Sets the eviction order.
	 * 
	 * @param asc
	 *            true (default) to evict the smallest element first, false to
	 *            evict the largest element first
	 */
	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	/**
	 * Returns the maximum number of elements.
	 * 
	 * @return the maximum number of elements
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Appends the element.
	 * 
	 * @return true if the element was not present before and has not been
	 *         evicted immediately
	 */
	@Override
	public boolean add(E e) {
		E eldestElement;
		boolean added;
		synchronized (keys) {
			delegate.add(e);
			added = keys.add(e);
			eldestElement = ensureCapacity(e, added);
		}
		if (eldestElement != null) {
			onEviction(eldestElement);
		}
		return added && eldestElement != e;
	}

	@Override
	public E set(int index, E element) {
		E previous;
		E eldestElement;
		synchronized (keys) {
			previous = delegate.set(index, element);
			if (previous != null) {
				keys.remove(previous);
			}
			boolean added = keys.add(element);
			if (!added) {
				// remove the other occurrence
				int first = delegate.indexOf(element);
				delegate.remove(first != index ? first : delegate.lastIndexOf(element));
			}
			eldestElement = ensureCapacity(element, added);
		}
		if (eldestElement != null) {
			onEviction(eldestElement);
		}
		return previous;
	}

	@Override
	public void add(int index, E element) {
		E eldestElement;
		synchronized (keys) {
			boolean added = keys.add(element);
			if (!added) {
				delegate.remove(element);
			}
			delegate.add(Math.min(index, delegate.size()), element);
			eldestElement = ensureCapacity(element, true);
		}
		if (eldestElement != null) {
			onEviction(eldestElement);
		}
	}

	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		synchronized (keys) {
			if (delegate.remove(o)) {
				keys.remove(o);
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		synchronized (keys) {
			if (delegate.removeAll(c)) {
				keys.removeAll(c);
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		synchronized (keys) {
			if (delegate.retainAll(c)) {
				keys.retainAll(c);
				return true;
			}
			return false;
		}
	}

	@Override
	public E remove(int index) {
		synchronized (keys) {
			E previous = delegate.remove(index);
			keys.remove(previous);
			return previous;
		}
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
		synchronized (keys) {
			delegate.clear();
			keys.clear();
		}
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

	@Override
	public String toString() {
		return delegate.toString();
	}

	private E ensureCapacity(E e, boolean added) {
		if (!added) {
			// duplicated element: keep only the latest occurrence
			if (delegate.indexOf(e) != delegate.lastIndexOf(e)) {
				delegate.remove(e);
			}
			return null;
		}
		if (keys.size() > maxSize) {
			E eldestElement = asc ? keys.pollFirst() : keys.pollLast();
			delegate.remove(eldestElement);
			return eldestElement;
		}
		return null;
	}

}
