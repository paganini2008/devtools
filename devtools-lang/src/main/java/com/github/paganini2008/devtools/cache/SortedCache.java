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
package com.github.paganini2008.devtools.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.ConcurrentSortedBoundedMap;

/**
 * 
 * SortedCache
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class SortedCache extends BoundedCache {

	private final ConcurrentSortedBoundedMap<Object, Object> boundedMap;

	public SortedCache(int maxSize) {
		this.boundedMap = new ConcurrentSortedBoundedMap<Object, Object>(new ConcurrentHashMap<Object, Object>(), maxSize) {

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
	public void setCacheStore(CacheStore store) {
		this.store = store;
	}

	public void setAsc(boolean asc) {
		this.boundedMap.setAsc(asc);
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent) {
			boundedMap.putIfAbsent(key, value);
		} else {
			boundedMap.put(key, value);
		}
	}

	public Object getObject(Object key) {
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

	public Object removeObject(Object key) {
		Object result = boundedMap.remove(key);
		if (result == null) {
			if (store != null) {
				result = store.removeObject(key);
			}
		}
		if (result != null) {
			dispose(key, result);
		}
		return result;
	}

	public int getSize() {
		return boundedMap.size() + (store != null ? store.getSize() : 0);
	}

	public void clear() {
		boundedMap.clear();
	}

	public Set<Object> keys() {
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(boundedMap.keySet());
		if (store != null) {
			keys.addAll(store.keys());
		}
		return Collections.unmodifiableSet(keys);
	}

	public boolean hasKey(Object key) {
		return (boundedMap.containsKey(key)) || (store != null && store.hasKey(key));
	}

	public String toString() {
		return boundedMap.toString();
	}

	public static void main(String[] args) {
		SortedCache cache = new SortedCache(10);
		for (int i = 0; i < 50; i++) {
			cache.putObject("Key_" + i, i);
		}
		System.out.println(cache);
		System.out.println("LifoCache.main()");
	}

}
