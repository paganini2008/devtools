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
package com.github.paganini2008.devtools.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * HashCache
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class HashCache extends AbstractCache {

	private final Map<Object, Object> cache;

	public HashCache() {
		this(new ConcurrentHashMap<Object, Object>());
	}

	public HashCache(Map<Object, Object> cache) {
		this.cache = cache;
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent) {
			cache.putIfAbsent(key, value);
		} else {
			cache.put(key, value);
		}
	}

	public Object getObject(Object key) {
		return cache.get(key);
	}

	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}

	public int getSize() {
		return cache.size();
	}

	public Set<Object> keys() {
		return cache.keySet();
	}

	public boolean hasKey(Object key) {
		return cache.containsKey(key);
	}

	public String toString() {
		return cache.toString();
	}

}
