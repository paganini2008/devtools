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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * LruSet
 * <p>
 * A set bounded by a maximum size which evicts the least recently used element
 * (added or looked up by {@link #contains(Object)}) when the size is exceeded.
 * Mutations are serialized by an internal lock.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public class LruSet<E> extends AbstractSet<E> implements Set<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = 1472051002956521109L;

	/**
	 * Creates a LruSet holding at most 128 elements.
	 */
	public LruSet() {
		this(128);
	}

	/**
	 * Creates a LruSet backed by a {@link CopyOnWriteArraySet}.
	 * 
	 * @param maxSize
	 *            the maximum number of elements, must be greater than zero
	 */
	public LruSet(final int maxSize) {
		this(new CopyOnWriteArraySet<E>(), maxSize);
	}

	public LruSet(final int maxSize, final LruMapSupplier<E, Object> supplier) {
		this(new CopyOnWriteArraySet<E>(), maxSize, supplier);
	}

	public LruSet(final Set<E> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	/**
	 * Creates a LruSet with a custom key tracker.
	 * 
	 * @param delegate
	 *            the set which actually holds the elements
	 * @param maxSize
	 *            the maximum number of elements
	 * @param supplier
	 *            creates the access-ordered map used to track the usage of
	 *            elements; it must invoke the given eviction listener when its
	 *            eldest key is dropped
	 */
	public LruSet(final Set<E> delegate, final int maxSize, final LruMapSupplier<E, Object> supplier) {
		this.delegate = delegate;
		this.keys = supplier.get(maxSize, (key, value) -> {
			delegate.remove(key);
			onEviction(key);
		});
	}

	private final Set<E> delegate;
	private final Map<E, Object> keys;

	@Override
	public boolean contains(Object o) {
		synchronized (keys) {
			if (delegate.contains(o)) {
				keys.get(o);
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean add(E e) {
		synchronized (keys) {
			if (delegate.add(e)) {
				keys.put(e, e);
				return true;
			}
			keys.get(e);
			return false;
		}
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
	public Iterator<E> iterator() {
		final Iterator<E> it = delegate.iterator();
		return new Iterator<E>() {

			private E current;
			private boolean removable;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public E next() {
				current = it.next();
				removable = true;
				return current;
			}

			@Override
			public void remove() {
				if (!removable) {
					throw new IllegalStateException();
				}
				synchronized (keys) {
					try {
						it.remove();
						keys.remove(current);
					} catch (UnsupportedOperationException e) {
						LruSet.this.remove(current);
					}
				}
				removable = false;
			}
		};
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public void clear() {
		synchronized (keys) {
			delegate.clear();
			keys.clear();
		}
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
