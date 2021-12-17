/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.RandomUtils;

/**
 * LruList
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class LruList<E> extends AbstractList<E> implements List<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -216068975490011223L;

	public LruList() {
		this(128);
	}

	public LruList(final int maxSize) {
		this(new CopyOnWriteArrayList<E>(), maxSize);
	}

	public LruList(final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this(new CopyOnWriteArrayList<E>(), maxSize, supplier);
	}

	public LruList(final List<E> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	public LruList(final List<E> delegate, final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this.delegate = delegate;
		this.keys = supplier.get(maxSize, (key, value) -> {
			delegate.remove(value);
			onEviction(value);
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

	@Override
	public Collection<E> getDelegate() {
		return delegate;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
	
	private static final Map<Integer, AtomicInteger> counter = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		LruList<Integer> list = new LruList<Integer>(20);
		for (int i = 0; i < 10000; i++) {
			int value = RandomUtils.randomInt(1,20);
			list.add(value);
			MapUtils.get(counter, value, () -> {
				return new AtomicInteger(0);
			}).incrementAndGet();
		}
		System.out.println(list);
		System.out.println("-------------------------------------");
		System.out.println(counter);
	}

}
