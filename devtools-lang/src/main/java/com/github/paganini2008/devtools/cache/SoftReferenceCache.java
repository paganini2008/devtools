/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.HashSet;
import java.util.Set;

import com.github.paganini2008.devtools.collection.SoftReferenceMap;

/**
 * 
 * SoftReferenceCache
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class SoftReferenceCache extends AbstractCache {

	private final SoftReferenceMap<Object, Object> cache;

	public SoftReferenceCache() {
		cache = new SoftReferenceMap<Object, Object>();
	}

	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent) {
			cache.putIfAbsent(key, value);
		} else {
			cache.put(key, value);
		}
	}

	public boolean hasKey(Object key) {
		return cache.containsKey(key);
	}

	public Object getObject(Object key) {
		return cache.get(key);
	}

	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	public Set<Object> keys() {
		return new HashSet<Object>(cache.keySet());
	}

	public void clear() {
		cache.clear();
	}

	public int getSize() {
		return cache.size();
	}

	public String toString() {
		return cache.toString();
	}

}
