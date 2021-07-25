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

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 
 * Cache
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Cache extends Iterable<Object> {

	default void putObject(Object key, Object value) {
		putObject(key, value, false);
	}

	void putObject(Object key, Object value, boolean ifAbsent);

	boolean hasKey(Object key);

	Object getObject(Object key);

	default Object getObject(Object key, Object defaultValue) {
		return getObject(key, () -> defaultValue);
	}

	default Object getObject(Object key, Supplier<Object> defaultValue) {
		Object o = getObject(key);
		if (o == null) {
			putObject(key, defaultValue.get());
			o = getObject(key);
		}
		return o;
	}

	Object removeObject(Object key);

	Set<Object> keys();

	Map<Object, Object> toEntries();

	void clear();

	int getSize();

	default void close() {
	}
}
