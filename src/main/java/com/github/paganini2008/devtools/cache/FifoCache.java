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
package com.github.paganini2008.devtools.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.ConcurrentBoundedMap;

/**
 * 
 * FifoCache
 * <p>
 * A bounded cache evicting the earliest inserted entry first (first in, first
 * out). Thread safe.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class FifoCache extends BoundedCache {

	private final ConcurrentBoundedMap<Object, Object> boundedMap;

	public FifoCache(int maxSize) {
		this.boundedMap = new ConcurrentBoundedMap<Object, Object>(new ConcurrentHashMap<Object, Object>(), maxSize) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Object eldestKey, Object eldestValue) {
				if (store != null) {
					store.writeObject(eldestKey, eldestValue);
				} else {
					dispose(eldestKey, eldestValue);
				}
			}

		};
	}

	private CacheStore store;

	@Override
	public synchronized void setCacheStore(CacheStore store) {
		this.store = store;
	}

	@Override
	synchronized Object peekObject(Object key) {
		if (key == null) {
			return null;
		}
		Object result = boundedMap.get(key);
		if (result == null && store != null) {
			result = store.readObject(key);
		}
		return result;
	}

	public synchronized int getSize() {
		return boundedMap.size() + (store != null ? store.getSize() : 0);
	}

	public synchronized void putObject(Object key, Object value, boolean ifAbsent) {
		if (key == null || value == null) {
			return;
		}
		if (ifAbsent) {
			if (store != null && store.hasKey(key)) {
				return;
			}
			boundedMap.putIfAbsent(key, value);
		} else {
			// Drop the stale copy in the store, otherwise it would be counted twice or revived after removal
			if (store != null && store.hasKey(key)) {
				store.removeObject(key);
			}
			boundedMap.put(key, value);
		}
	}

	public synchronized Object getObject(Object key) {
		if (key == null) {
			return null;
		}
		Object result = boundedMap.get(key);
		if (result == null) {
			if (store != null) {
				result = store.readObject(key);
				if (result != null) {
					store.removeObject(key);
					putObject(key, result);
				}
			}
		}
		return result;
	}

	public synchronized Object removeObject(Object key) {
		if (key == null) {
			return null;
		}
		Object result = boundedMap.remove(key);
		if (store != null && store.hasKey(key)) {
			Object stored = store.removeObject(key);
			if (result == null) {
				result = stored;
			}
		}
		if (result != null) {
			dispose(key, result);
		}
		return result;
	}

	public synchronized void clear() {
		boundedMap.clear();
		if (store != null) {
			for (Object key : new ArrayList<Object>(store.keys())) {
				store.removeObject(key);
			}
		}
	}

	public synchronized Set<Object> keys() {
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(boundedMap.keySet());
		if (store != null) {
			keys.addAll(store.keys());
		}
		return Collections.unmodifiableSet(keys);
	}

	public synchronized boolean hasKey(Object key) {
		if (key == null) {
			return false;
		}
		return (boundedMap.containsKey(key)) || (store != null && store.hasKey(key));
	}

	public synchronized String toString() {
		return boundedMap.toString();
	}

}
