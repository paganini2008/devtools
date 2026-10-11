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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * ConcurrentBoundedList
 * <p>
 * A thread-safe list bounded by a maximum size which evicts the earliest
 * inserted element (FIFO) when the size is exceeded.
 *
 * @author Fred Feng
 * @date 2021-12-16
 * @since 2.0.4
 */
public class ConcurrentBoundedList<E> extends AbstractList<E> implements Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = 4364094168351330996L;

	private final List<E> delegate;
	private final int maxSize;
	private final Queue<E> keys;

	/**
	 * Creates a ConcurrentBoundedList backed by a {@link CopyOnWriteArrayList}.
	 * 
	 * @param maxSize
	 *            the maximum number of elements
	 */
	public ConcurrentBoundedList(int maxSize) {
		this(new CopyOnWriteArrayList<E>(), maxSize);
	}

	public ConcurrentBoundedList(List<E> delegate, int maxSize) {
		this(delegate, maxSize, new ConcurrentLinkedQueue<E>());
	}

	/**
	 * Creates a ConcurrentBoundedList.
	 * 
	 * @param delegate
	 *            the list which actually holds the elements
	 * @param maxSize
	 *            the maximum number of elements
	 * @param keys
	 *            the queue which records the insertion order
	 */
	public ConcurrentBoundedList(List<E> delegate, int maxSize, Queue<E> keys) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = keys;
	}

	/**
	 * Returns the maximum number of elements.
	 * 
	 * @return the maximum number of elements
	 */
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public boolean add(E e) {
		E eldestElement;
		boolean result;
		synchronized (keys) {
			result = delegate.add(e);
			eldestElement = ensureCapacity(e);
		}
		if (eldestElement != null) {
			onEviction(eldestElement);
		}
		return result;
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
			eldestElement = ensureCapacity(element);
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
			delegate.add(index, element);
			eldestElement = ensureCapacity(element);
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

	private E ensureCapacity(E e) {
		keys.add(e);
		if (keys.size() > maxSize) {
			E eldestElement = keys.poll();
			delegate.remove(eldestElement);
			return eldestElement;
		}
		return null;
	}

}
