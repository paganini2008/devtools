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
package com.github.paganini2008.devtools.comparator;

import java.util.Comparator;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * AbstractComparator
 * <p>
 * Base class of comparators providing in-place sorting helpers.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class AbstractComparator<T> implements Comparator<T> {

	/**
	 * Sorts the array in place, ignoring a null or empty array.
	 *
	 * @return the given array
	 */
	public T[] sort(T[] args) {
		if (ArrayUtils.isNotEmpty(args)) {
			ArrayUtils.sort(args, this);
		}
		return args;
	}

	/**
	 * Sorts the list in place, ignoring a null or empty list.
	 *
	 * @return the given list
	 */
	public List<T> sort(List<T> args) {
		if (ListUtils.isNotEmpty(args)) {
			ListUtils.sort(args, this);
		}
		return args;
	}

	/**
	 * Returns a comparator imposing the reverse order.
	 */
	public Comparator<T> reverse() {
		return this.reversed();
	}

	public String toString() {
		return "[Comparator] " + getClass().getName();
	}

}
