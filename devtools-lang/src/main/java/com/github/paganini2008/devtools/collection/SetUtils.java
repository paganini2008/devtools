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
package com.github.paganini2008.devtools.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.paganini2008.devtools.ArrayUtils;

/**
 * SetUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class SetUtils {

	public static <T> Set<T> emptySet() {
		return Collections.EMPTY_SET;
	}

	public static <T> Set<T> unmodifiableSet(T... args) {
		return Collections.unmodifiableSet(create(args));
	}

	public static <T> Set<T> unmodifiableSet(Collection<T> c) {
		return Collections.unmodifiableSet(toSet(c));
	}

	public static <T> Set<T> synchronizedSet() {
		return Collections.synchronizedSet(new HashSet<T>());
	}

	public static <T> Set<T> synchronizedSet(Collection<T> c) {
		return Collections.synchronizedSet(toSet(c));
	}

	public static <T> Set<T> create(T... args) {
		return ArrayUtils.isNotEmpty(args) ? new HashSet<T>(Arrays.asList(args)) : new HashSet<T>();
	}

	public static <T> Set<T> toSet(Collection<T> c) {
		return c instanceof Set ? (Set<T>) c : new HashSet(c);
	}

	public static boolean isSet(Object obj) {
		return obj == null ? false : obj instanceof Set;
	}

	public static boolean isNotSet(Object obj) {
		return !isSet(obj);
	}

}
