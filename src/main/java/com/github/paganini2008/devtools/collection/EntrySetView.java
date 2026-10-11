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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * EntrySetView
 * <p>
 * An entry set view over a delegate map whose removals are routed through a
 * callback, so that a wrapping map can keep its own bookkeeping consistent.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
class EntrySetView<K, V> extends AbstractSet<Map.Entry<K, V>> {

	private final Map<K, V> delegate;
	private final Consumer<K> remover;

	EntrySetView(Map<K, V> delegate, Consumer<K> remover) {
		this.delegate = delegate;
		this.remover = remover;
	}

	@Override
	public Iterator<Map.Entry<K, V>> iterator() {
		final Iterator<Map.Entry<K, V>> it = delegate.entrySet().iterator();
		return new Iterator<Map.Entry<K, V>>() {

			private Map.Entry<K, V> current;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Map.Entry<K, V> next() {
				current = it.next();
				return current;
			}

			@Override
			public void remove() {
				if (current == null) {
					throw new IllegalStateException();
				}
				remover.accept(current.getKey());
				current = null;
			}
		};
	}

	@Override
	public boolean contains(Object o) {
		return delegate.entrySet().contains(o);
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Map.Entry && delegate.entrySet().contains(o)) {
			@SuppressWarnings("unchecked")
			K key = ((Map.Entry<K, V>) o).getKey();
			remover.accept(key);
			return true;
		}
		return false;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public void clear() {
		List<K> keys = new ArrayList<K>(delegate.keySet());
		for (K key : keys) {
			remover.accept(key);
		}
	}

}
