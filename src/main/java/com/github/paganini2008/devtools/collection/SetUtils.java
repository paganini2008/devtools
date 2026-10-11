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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.paganini2008.devtools.ArrayUtils;

/**
 * SetUtils
 * <p>
 * Utility methods for sets.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class SetUtils {

	/**
	 * Returns the immutable empty set.
	 */
	public static <T> Set<T> emptySet() {
		return Collections.EMPTY_SET;
	}

	/**
	 * Returns an unmodifiable set of the given elements.
	 */
	public static <T> Set<T> unmodifiableSet(T... args) {
		return Collections.unmodifiableSet(create(args));
	}

	/**
	 * Returns an unmodifiable view of the collection as a set.
	 */
	public static <T> Set<T> unmodifiableSet(Collection<T> c) {
		return Collections.unmodifiableSet(toSet(c));
	}

	/**
	 * Returns a new synchronized HashSet.
	 */
	public static <T> Set<T> synchronizedSet() {
		return Collections.synchronizedSet(new HashSet<T>());
	}

	/**
	 * Returns a synchronized view of the collection as a set.
	 */
	public static <T> Set<T> synchronizedSet(Collection<T> c) {
		return Collections.synchronizedSet(toSet(c));
	}

	/**
	 * Returns a new mutable HashSet holding the given elements.
	 */
	public static <T> Set<T> create(T... args) {
		return ArrayUtils.isNotEmpty(args) ? new HashSet<T>(Arrays.asList(args)) : new HashSet<T>();
	}

	/**
	 * Returns the collection itself if it is a Set, otherwise a new HashSet copied from it (empty for null).
	 */
	public static <T> Set<T> toSet(Collection<T> c) {
		if (c == null) {
			return new HashSet<T>();
		}
		return c instanceof Set ? (Set<T>) c : new HashSet(c);
	}

	/**
	 * Checks if the object is a Set (false for null).
	 */
	public static boolean isSet(Object obj) {
		return obj == null ? false : obj instanceof Set;
	}

	public static boolean isNotSet(Object obj) {
		return !isSet(obj);
	}

}
