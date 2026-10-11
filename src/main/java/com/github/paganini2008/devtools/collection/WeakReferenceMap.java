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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * WeakReferenceMap
 * <p>
 * A thread-safe map whose values are held by {@link WeakReference weak
 * references}. Mappings whose values have been reclaimed are purged on the
 * next write, and {@link #onEviction(Object, Object)} is invoked for each of
 * them.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class WeakReferenceMap<K, V> extends ReferenceMap<K, V> implements Map<K, V> {

	private final ReferenceQueue<V> rq;

	public WeakReferenceMap() {
		super(new ConcurrentHashMap<K, Reference<V>>());
		this.rq = new ReferenceQueue<V>();
	}

	public V put(K key, V value) {
		V prev = super.put(key, value);
		expungeStaleEntries();
		return prev;
	}

	public V remove(Object key) {
		V prev = super.remove(key);
		expungeStaleEntries();
		return prev;
	}

	@SuppressWarnings("unchecked")
	private void expungeStaleEntries() {
		Ref ref;
		while ((ref = (Ref) rq.poll()) != null) {
			if (removeReference(ref.key, ref)) {
				onEviction(ref.key, null);
			}
		}
	}

	/**
	 * Invoked when a mapping is purged because its value has been reclaimed.
	 * 
	 * @param key
	 *            the key of the purged mapping
	 * @param value
	 *            always null as the value has been reclaimed
	 */
	protected void onEviction(K key, V value) {
	}

	class Ref extends WeakReference<V> {

		final K key;

		Ref(K key, V value, ReferenceQueue<V> rq) {
			super(value, rq);
			this.key = key;
		}

	}

	protected Reference<V> fold(K key, V value) {
		return new Ref(key, value, rq);
	}

}
