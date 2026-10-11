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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MemoryCacheStore, an in-memory {@link CacheStore} recording the written keys
 * for tests
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MemoryCacheStore implements CacheStore {

	final Map<Object, Object> data = Collections.synchronizedMap(new LinkedHashMap<Object, Object>());
	final List<Object> written = Collections.synchronizedList(new ArrayList<Object>());

	@Override
	public void writeObject(Object key, Object value) {
		written.add(key);
		data.put(key, value);
	}

	@Override
	public Object readObject(Object key) {
		return data.get(key);
	}

	@Override
	public Object removeObject(Object key) {
		return data.remove(key);
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public Set<Object> keys() {
		synchronized (data) {
			return new LinkedHashSet<Object>(data.keySet());
		}
	}

	@Override
	public boolean hasKey(Object key) {
		return data.containsKey(key);
	}

}
