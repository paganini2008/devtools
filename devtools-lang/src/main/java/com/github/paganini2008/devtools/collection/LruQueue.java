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
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * LruQueue
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class LruQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -6416314527516614700L;

	public LruQueue() {
		this(128);
	}

	public LruQueue(final int maxSize) {
		this(new ConcurrentLinkedQueue<E>(), maxSize);
	}

	public LruQueue(final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this(new ConcurrentLinkedQueue<E>(), maxSize, supplier);
	}

	public LruQueue(final Queue<E> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	public LruQueue(final Queue<E> delegate, final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this.delegate = delegate;
		this.keys = supplier.get(maxSize, (key, value) -> {
			delegate.remove(value);
			onEviction(value);
		});
	}

	private final Queue<E> delegate;
	private final Map<Integer, E> keys;
	private int index = 0;

	@Override
	public boolean offer(E e) {
		synchronized (keys) {
			keys.put(index++, e);
			return delegate.offer(e);
		}
	}

	@Override
	public E poll() {
		E e;
		if ((e = delegate.poll()) != null) {
			MapUtils.removeLastEntry(keys);
		}
		return e;
	}

	@Override
	public E peek() {
		E e;
		if ((e = delegate.peek()) != null) {
			keys.get(MapUtils.getLastEntry(keys).getKey());
		}
		return e;
	}

	@Override
	public boolean contains(Object o) {
		if (delegate.contains(o)) {
			keys.get(indexFor(o));
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (delegate.remove(o)) {
			keys.remove(indexFor(o));
			return true;
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	private int indexFor(Object o) {
		Integer result = MapUtils.getKey(keys, o);
		return result != null ? result.intValue() : -1;
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
