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
package com.github.paganini2008.devtools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 
 * Provider
 * 
 * A thread-safe, caching {@link java.util.function.Function}: the value of each key is created once by
 * {@link #createObject(Object)} and reused afterwards. Null values are not cached.
 * 
 * @author Fred Feng
 * @date 2021-05-04
 *
 * @since 2.0.1
 */
public abstract class Provider<T, R> implements Function<T, R> {

	private final Map<T, R> cache = new ConcurrentHashMap<T, R>();

	@Override
	public R apply(T key) {
		R result = cache.get(key);
		if (result == null) {
			R newResult = createObject(key);
			if (newResult == null) {
				return null;
			}
			result = cache.putIfAbsent(key, newResult);
			if (result == null) {
				result = newResult;
			}
		}
		return result;
	}

	/**
	 * Creates the value for the given key.
	 *
	 * @param key the key
	 * @return the value, may be null (null values are not cached)
	 */
	protected abstract R createObject(T key);

}
