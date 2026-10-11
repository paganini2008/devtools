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
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * LruQueue
 * <p>
 * A FIFO queue bounded by a maximum size which evicts the least recently used
 * element when the size is exceeded. An element is regarded as used when it is
 * offered, peeked or found by {@link #contains(Object)}. Mutations are
 * serialized by an internal lock. Iterating does not change the usage order.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public class LruQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -6416314527516614700L;

	/**
	 * Creates a LruQueue holding at most 128 elements.
	 */
	public LruQueue() {
		this(128);
	}

	/**
	 * Creates a LruQueue backed by a {@link ConcurrentLinkedQueue}.
	 * 
	 * @param maxSize
	 *            the maximum number of elements, must be greater than zero
	 */
	public LruQueue(final int maxSize) {
		this(new ConcurrentLinkedQueue<E>(), maxSize);
	}

	public LruQueue(final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this(new ConcurrentLinkedQueue<E>(), maxSize, supplier);
	}

	public LruQueue(final Queue<E> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	/**
	 * Creates a LruQueue with a custom usage tracker. Elements already in the
	 * delegate queue are kept (the eldest ones are evicted if there are more
	 * than <code>maxSize</code>).
	 * 
	 * @param delegate
	 *            the queue which actually holds the elements, its iterator
	 *            must support removal
	 * @param maxSize
	 *            the maximum number of elements
	 * @param supplier
	 *            creates the access-ordered map used to track the usage of
	 *            elements; it must invoke the given eviction listener when its
	 *            eldest key is dropped
	 */
	public LruQueue(final Queue<E> delegate, final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this.delegate = delegate;
		this.keys = supplier.get(maxSize, (id, value) -> {
			int position = ids.indexOf(id);
			if (position >= 0) {
				removeAt(position);
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

	private final Queue<E> delegate;
	private final Map<Integer, E> keys;
	private final LinkedList<Integer> ids = new LinkedList<Integer>();
	private int sequence = 0;

	@Override
	public boolean offer(E e) {
		synchronized (keys) {
			if (!delegate.offer(e)) {
				return false;
			}
			int id = sequence++;
			ids.add(id);
			keys.put(id, e);
			return true;
		}
	}

	@Override
	public E poll() {
		synchronized (keys) {
			E e = delegate.poll();
			if (e != null) {
				keys.remove(ids.pollFirst());
			}
			return e;
		}
	}

	@Override
	public E peek() {
		synchronized (keys) {
			E e = delegate.peek();
			if (e != null) {
				keys.get(ids.peekFirst());
			}
			return e;
		}
	}

	@Override
	public boolean contains(Object o) {
		synchronized (keys) {
			int position = indexOf(o);
			if (position < 0) {
				return false;
			}
			keys.get(ids.get(position));
			return true;
		}
	}

	@Override
	public boolean remove(Object o) {
		synchronized (keys) {
			int position = indexOf(o);
			if (position < 0) {
				return false;
			}
			keys.remove(removeAt(position));
			return true;
		}
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
	public int size() {
		return delegate.size();
	}

	private int indexOf(Object o) {
		int position = 0;
		for (E e : delegate) {
			if (Objects.equals(e, o)) {
				return position;
			}
			position++;
		}
		return -1;
	}

	private Integer removeAt(int position) {
		Iterator<E> it = delegate.iterator();
		for (int i = 0; i <= position; i++) {
			it.next();
		}
		it.remove();
		return ids.remove(position);
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
