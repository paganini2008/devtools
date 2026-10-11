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

/**
 * ComparatorHelper
 * <p>
 * Helper methods for writing comparators.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class ComparatorHelper {

	/**
	 * Returns a comparator regarding all objects as equal.
	 */
	public static <T> Comparator<T> identity() {
		return (a, b) -> 0;
	}

	/**
	 * Normalizes a difference to -1, 0 or 1 (NaN is regarded as 0).
	 *
	 * <pre>
	 * ComparatorHelper.valueOf(-0.5) = -1
	 * ComparatorHelper.valueOf(2.5)  = 1
	 * ComparatorHelper.valueOf(0.0)  = 0
	 * </pre>
	 */
	public static int valueOf(double result) {
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Normalizes a difference to -1, 0 or 1.
	 */
	public static int valueOf(long result) {
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Compares two comparable objects, normalizing the result to -1, 0 or 1. Null is
	 * regarded as the smallest value.
	 *
	 * <pre>
	 * ComparatorHelper.compareTo(1, 5)       = -1
	 * ComparatorHelper.compareTo(3, null)    = 1
	 * ComparatorHelper.compareTo(null, null) = 0
	 * </pre>
	 */
	public static <T extends Comparable<T>> int compareTo(T a, T b) {
		if (a != null && b != null) {
			return valueOf(a.compareTo(b));
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}

	/**
	 * Compares two strings, optionally ignoring case, normalizing the result to -1,
	 * 0 or 1. Null is regarded as the smallest value.
	 *
	 * <pre>
	 * ComparatorHelper.compareTo("A", "a", true)  = 0
	 * ComparatorHelper.compareTo("A", "a", false) = -1
	 * </pre>
	 */
	public static int compareTo(String a, String b, boolean ignoreCase) {
		if (a != null && b != null) {
			return valueOf(ignoreCase ? a.compareToIgnoreCase(b) : a.compareTo(b));
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}
}
