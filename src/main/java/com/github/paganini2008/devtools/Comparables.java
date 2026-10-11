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

import java.util.Collection;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * Null-safe comparison helpers for {@link Comparable} values, where null is
 * regarded as less than any non-null value.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 *
 * @since 2.0.1
 */
public abstract class Comparables {

	/**
	 * Returns the left value if it is not null, otherwise the right value. Intended
	 * for computing a maximum when one value is null.
	 */
	public static <T> T nullOrMax(T leftValue, T rightValue) {
		return leftValue == null ? rightValue : leftValue;
	}

	/**
	 * Returns null if the left value is null, otherwise the right value. Intended
	 * for computing a minimum when one value is null (null is the minimum).
	 */
	public static <T> T nullOrMin(T leftValue, T rightValue) {
		return leftValue == null ? leftValue : rightValue;
	}

	/**
	 * Returns the left value, or null if both values are equal.
	 */
	public static <T extends Comparable<T>> T getOrDefault(T left, T right) {
		return getOrDefault(left, right, null);
	}

	/**
	 * Returns the left value, or the default value if both values are equal.
	 */
	public static <T extends Comparable<T>> T getOrDefault(T left, T right, T defaultValue) {
		if (compareTo(left, right) == 0) {
			return defaultValue;
		}
		return left;
	}

	/**
	 * Returns the default value if both values are equal, otherwise the smaller of
	 * {@code left} and {@code defaultValue}.
	 */
	public static <T extends Comparable<T>> T minOrDefault(T left, T right, T defaultValue) {
		if (compareTo(left, right) == 0) {
			return defaultValue;
		}
		return min(left, defaultValue);
	}

	/**
	 * Returns the default value if both values are equal, otherwise the larger of
	 * {@code left} and {@code defaultValue}.
	 */
	public static <T extends Comparable<T>> T maxOrDefault(T left, T right, T defaultValue) {
		if (compareTo(left, right) == 0) {
			return defaultValue;
		}
		return max(left, defaultValue);
	}

	/**
	 * Returns the maximum element, or null if the array is null or empty.
	 */
	public static <T extends Comparable<T>> T max(T[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return null;
		}
		T max = array[0];
		for (T t : array) {
			max = max(max, t);
		}
		return max;
	}

	/**
	 * Returns the minimum element (null if present), or null if the array is null or
	 * empty.
	 */
	public static <T extends Comparable<T>> T min(T[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return null;
		}
		T min = array[0];
		for (T t : array) {
			min = min(min, t);
		}
		return min;
	}

	/**
	 * Returns the larger value; null is regarded as the smallest.
	 */
	public static <T extends Comparable<T>> T max(T a, T b) {
		return compareTo(a, b) >= 0 ? a : b;
	}

	/**
	 * Returns the smaller value; null is regarded as the smallest.
	 */
	public static <T extends Comparable<T>> T min(T a, T b) {
		return compareTo(a, b) <= 0 ? a : b;
	}

	/**
	 * Checks whether the value is equal to any element. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean eqAny(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (eq(value, t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the value is equal to all elements. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean eqAll(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (ne(value, t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the value is less than any element. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean ltAny(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (lt(value, t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the value is less than all elements. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean ltAll(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (gte(value, t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the value is greater than any element. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean gtAny(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (gt(value, t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the value is greater than all elements. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean gtAll(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (lte(value, t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the value is less than or equal to any element. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean lteAny(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (lte(value, t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the value is less than or equal to all elements. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean lteAll(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (gt(value, t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the value is greater than or equal to any element. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean gteAny(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (gte(value, t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the value is greater than or equal to all elements. An empty collection returns
	 * false.
	 */
	public static <T extends Comparable<T>> boolean gteAll(T value, Collection<T> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		for (T t : c) {
			if (lt(value, t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the value is within [minValue, maxValue].
	 */
	public static <T extends Comparable<T>> boolean between(T value, T minValue, T maxValue) {
		return between(value, minValue, maxValue, false);
	}

	/**
	 * Checks whether the value is within the range, excluding the bounds if
	 * {@code exclusive}.
	 */
	public static <T extends Comparable<T>> boolean between(T value, T minValue, T maxValue, boolean exclusive) {
		if (exclusive) {
			return gt(value, minValue) && lt(value, maxValue);
		}
		return gte(value, minValue) && lte(value, maxValue);
	}

	/**
	 * Checks whether two values are not equal (null-safe).
	 */
	public static <T extends Comparable<T>> boolean ne(T left, T right) {
		return compareTo(left, right) != 0;
	}

	/**
	 * Checks whether two values are equal (null-safe).
	 */
	public static <T extends Comparable<T>> boolean eq(T left, T right) {
		return compareTo(left, right) == 0;
	}

	/**
	 * Checks whether left &gt;= right (null-safe).
	 */
	public static <T extends Comparable<T>> boolean gte(T left, T right) {
		return compareTo(left, right) >= 0;
	}

	/**
	 * Checks whether left &gt; right (null-safe).
	 */
	public static <T extends Comparable<T>> boolean gt(T left, T right) {
		return compareTo(left, right) > 0;
	}

	/**
	 * Checks whether left &lt;= right (null-safe).
	 */
	public static <T extends Comparable<T>> boolean lte(T left, T right) {
		return compareTo(left, right) <= 0;
	}

	/**
	 * Checks whether left &lt; right (null-safe).
	 */
	public static <T extends Comparable<T>> boolean lt(T left, T right) {
		return compareTo(left, right) < 0;
	}

	/**
	 * Compares two values, returning -1, 0 or 1; null is regarded as the smallest.
	 *
	 * <pre>
	 * Comparables.compareTo(null, null) = 0
	 * Comparables.compareTo(null, 1)    = -1
	 * Comparables.compareTo("z", "a")   = 1
	 * </pre>
	 */
	public static <T extends Comparable<T>> int compareTo(T left, T right) {
		if (left == null && right != null) {
			return -1;
		} else if (left != null && right == null) {
			return 1;
		} else if (left != null && right != null) {
			return compareResult(left.compareTo(right));
		}
		return 0;
	}

	private static <T extends Comparable<T>> int compareResult(int result) {
		if (result < 0) {
			return -1;
		}
		if (result > 0) {
			return 1;
		}
		return 0;
	}

}
