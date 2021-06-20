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
package com.github.paganini2008.devtools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 
 * Provider
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public abstract class Provider<T, R> implements Function<T, R> {

	private final Map<T, R> cache = new ConcurrentHashMap<T, R>();

	@Override
	public R apply(T key) {
		R result = cache.get(key);
		if (result == null) {
			cache.putIfAbsent(key, createObject(key));
			result = cache.get(key);
		}
		return null;
	}

	protected abstract R createObject(T key);

}
