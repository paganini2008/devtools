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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * LruList
 * <p>
 * A list bounded by a maximum size which evicts the least recently used
 * element when the size is exceeded. An element is regarded as used when it is
 * added, replaced, read by {@link #get(int)} or found by
 * {@link #contains(Object)}. Mutations are serialized by an internal lock.
 * Iterating does not change the usage order, and the list iterator returned
 * is read-only.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public class LruList<E> extends AbstractList<E> implements List<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -216068975490011223L;

	/**
	 * Creates a LruList holding at most 128 elements.
	 */
	public LruList() {
		this(128);
	}

	/**
	 * Creates a LruList backed by a {@link CopyOnWriteArrayList}.
	 * 
	 * @param maxSize
	 *            the maximum number of elements, must be greater than zero
	 */
	public LruList(final int maxSize) {
		this(new CopyOnWriteArrayList<E>(), maxSize);
	}

	public LruList(final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this(new CopyOnWriteArrayList<E>(), maxSize, supplier);
	}

	public LruList(final List<E> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	/**
	 * Creates a LruList with a custom usage tracker. Elements already in the
	 * delegate list are kept (the eldest ones are evicted if there are more
	 * than <code>maxSize</code>).
	 * 
	 * @param delegate
	 *            the list which actually holds the elements
	 * @param maxSize
	 *            the maximum number of elements
	 * @param supplier
	 *            creates the access-ordered map used to track the usage of
	 *            elements; it must invoke the given eviction listener when its
	 *            eldest key is dropped
	 */
	public LruList(final List<E> delegate, final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this.delegate = delegate;
		this.keys = supplier.get(maxSize, (id, value) -> {
			int position = ids.indexOf(id);
			if (position >= 0) {
				ids.remove(position);
				delegate.remove(position);
			}
			onEviction(value);
		});
		synchronized (keys) {
			for (E e : new ArrayList<E>(delegate)) {
				int id = sequence++;
				ids.add(id);
				keys.put(id, e);
			}
		}
	}

	private final List<E> delegate;
	private final Map<Integer, E> keys;
	private final List<Integer> ids = new ArrayList<Integer>();
	private int sequence = 0;

	@Override
	public boolean add(E e) {
		synchronized (keys) {
			delegate.add(e);
			int id = sequence++;
			ids.add(id);
			keys.put(id, e);
			return true;
		}
	}

	@Override
	public void add(int index, E e) {
		synchronized (keys) {
			delegate.add(index, e);
			int id = sequence++;
			ids.add(index, id);
			keys.put(id, e);
		}
	}

	@Override
	public E set(int index, E e) {
		synchronized (keys) {
			E previous = delegate.set(index, e);
			keys.put(ids.get(index), e);
			return previous;
		}
	}

	@Override
	public E get(int index) {
		synchronized (keys) {
			E e = delegate.get(index);
			keys.get(ids.get(index));
			return e;
		}
	}

	@Override
	public E remove(int index) {
		synchronized (keys) {
			E e = delegate.remove(index);
			keys.remove(ids.remove(index));
			return e;
		}
	}

	@Override
	public boolean remove(Object o) {
		synchronized (keys) {
			int index = delegate.indexOf(o);
			if (index < 0) {
				return false;
			}
			remove(index);
			return true;
		}
	}

	@Override
	public boolean contains(Object o) {
		synchronized (keys) {
			int index = delegate.indexOf(o);
			if (index < 0) {
				return false;
			}
			keys.get(ids.get(index));
			return true;
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
			ids.clear();
			keys.clear();
		}
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> it = delegate.iterator();
		return new Iterator<E>() {

			private int cursor = -1;
			private boolean removable;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public E next() {
				E e = it.next();
				cursor++;
				removable = true;
				return e;
			}

			@Override
			public void remove() {
				if (!removable) {
					throw new IllegalStateException();
				}
				synchronized (keys) {
					it.remove();
					keys.remove(ids.remove(cursor--));
				}
				removable = false;
			}
		};
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return Collections.unmodifiableList(delegate).listIterator(index);
	}

	@Override
	public Collection<E> getDelegate() {
		return delegate;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

}
