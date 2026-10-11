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

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 
 * Cache
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Cache extends Iterable<Object> {

	/**
	 * Puts a value, replacing the existing one.
	 */
	default void putObject(Object key, Object value) {
		putObject(key, value, false);
	}

	/**
	 * Puts a value. If ifAbsent is true, an existing value is kept.
	 * <p>
	 * Note: most implementations ignore a null key or value.
	 */
	void putObject(Object key, Object value, boolean ifAbsent);

	/**
	 * Returns true if the key exists in this cache (including the secondary storage if any).
	 * Returns false for a null key.
	 */
	boolean hasKey(Object key);

	/**
	 * Returns the value of the key, or null if absent (or the key is null).
	 */
	Object getObject(Object key);

	/**
	 * Returns the value of the key; if absent, the default value is put and returned. A null
	 * default value is not put.
	 */
	default Object getObject(Object key, Object defaultValue) {
		return getObject(key, () -> defaultValue);
	}

	/**
	 * Returns the value of the key; if absent, the value of the supplier is put and returned. A
	 * null supplied value is not put.
	 */
	default Object getObject(Object key, Supplier<Object> defaultValue) {
		Object o = getObject(key);
		if (o == null) {
			Object value = defaultValue.get();
			if (value == null) {
				return null;
			}
			putObject(key, value);
			o = getObject(key);
		}
		return o;
	}

	/**
	 * Removes the key and returns its value, or null if absent.
	 */
	Object removeObject(Object key);

	/**
	 * Returns the keys of this cache.
	 */
	Set<Object> keys();

	/**
	 * Returns a snapshot of the entries.
	 */
	Map<Object, Object> toEntries();

	/**
	 * Removes all the entries (including those in the secondary storage if any).
	 */
	void clear();

	/**
	 * Returns the number of entries (including those in the secondary storage if any).
	 */
	int getSize();

	/**
	 * Releases the resources of this cache. Does nothing by default.
	 */
	default void close() {
	}
}
