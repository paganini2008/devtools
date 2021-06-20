/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * LruSet
 * 
 * @author Fred Feng
 * 
 * @version 1.0
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
		this(delegate, maxSize,
				(size, listener) -> Collections.synchronizedMap(MapUtils.newLruMap(16, size, listener)));
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

	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	public int size() {
		return delegate.size();
	}

	public boolean contains(Object o) {
		keys.get(o);
		return delegate.contains(o);
	}

	public boolean add(E e) {
		keys.put(e, e);
		return delegate.add(e);
	}

	public boolean remove(Object o) {
		keys.remove(o);
		return delegate.remove(o);
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public String toString() {
		return delegate.toString();
	}

}
