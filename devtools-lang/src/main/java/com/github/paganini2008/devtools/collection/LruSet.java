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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.RandomUtils;

/**
 * LruSet
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class LruSet<E> extends AbstractSet<E> implements Set<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = 1472051002956521109L;

	public LruSet() {
		this(128);
	}

	public LruSet(final int maxSize) {
		this(new CopyOnWriteArraySet<E>(), maxSize);
	}

	public LruSet(final int maxSize, final LruMapSupplier<E, Object> supplier) {
		this(new CopyOnWriteArraySet<E>(), maxSize, supplier);
	}

	public LruSet(final Set<E> delegate, final int maxSize) {
		this(delegate, maxSize, (size, listener) -> MapUtils.synchronizedLinkedHashMap(16, size, listener));
	}

	public LruSet(final Set<E> delegate, final int maxSize, final LruMapSupplier<E, Object> supplier) {
		this.delegate = delegate;
		this.keys = supplier.get(maxSize, (key, value) -> {
			delegate.remove(key);
			onEviction(key);
		});
	}

	private final Set<E> delegate;
	private final Map<E, Object> keys;

	public boolean contains(Object o) {
		if (delegate.contains(o)) {
			keys.get(o);
			return true;
		}
		return false;
	}

	public boolean add(E e) {
		if (delegate.add(e)) {
			keys.put(e, e);
			return true;
		}
		return false;
	}

	public boolean remove(Object o) {
		if (delegate.remove(o)) {
			keys.remove(o);
			return true;
		}
		return false;
	}

	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	public int size() {
		return delegate.size();
	}

	public void clear() {
		delegate.clear();
		keys.clear();
	}

	@Override
	public Collection<E> getDelegate() {
		return delegate;
	}

	public String toString() {
		return delegate.toString();
	}

	private static final Map<Integer, AtomicInteger> counter = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		LruSet<Integer> list = new LruSet<Integer>(20);
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
