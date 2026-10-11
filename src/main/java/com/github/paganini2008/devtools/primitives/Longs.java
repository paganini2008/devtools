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
package com.github.paganini2008.devtools.primitives;

import static com.github.paganini2008.devtools.ArrayUtils.INDEX_NOT_FOUND;
import static com.github.paganini2008.devtools.ArrayUtils.MERGE_SORT_THRESHOLD;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.NumberOverflowException;
import com.github.paganini2008.devtools.NumberRangeAssert;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Null-safe utility methods for {@code long} values and arrays: searching, copying, sorting, set operations,
 * lenient parsing, range-checked casting and bit manipulation helpers.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Longs {

	public static final long[] EMPTY_LONG_ARRAY = new long[0];

	public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];

	public static final int BYTES = Long.SIZE / Byte.SIZE;

	public static final long MAX_POWER_OF_TWO = 1L << (Long.SIZE - 2);

	private static final LruMap<String, Long> cache = new LruMap<String, Long>(1024);

	/**
	 * Clears the internal cache of parsed strings.
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static long[] clone(long[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(long[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static long[][] create(int yLength, int xLength, long defaultValue) {
		long[][] array = new long[yLength][];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static long[] create(int length, long defaultValue) {
		long[] array = new long[length];
		if (defaultValue != 0) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	/**
	 * Checks if an array is neither {@code null} nor empty.
	 */
	public static boolean isNotEmpty(long[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 * <pre>
	 * Longs.isEmpty(null)   = true
	 * Longs.isEmpty([])     = true
	 * Longs.isEmpty([1])    = false
	 * </pre>
	 */
	public static boolean isEmpty(long[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(long[] a, long b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(long[] a, long b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 * <pre>
	 * Longs.indexOf(null, 1)      = -1
	 * Longs.indexOf([1, 2, 1], 1) = 0
	 * Longs.indexOf([1, 2], 3)    = -1
	 * </pre>
	 */
	public static int indexOf(long[] a, long b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(long[] a, long b, int start, int end) {
		if (a == null) {
			return INDEX_NOT_FOUND;
		}
		if (start < 0) {
			return INDEX_NOT_FOUND;
		}
		for (int i = start, l = Math.min(a.length, end); i < l; i++) {
			if (a[i] == b) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the last index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 * <pre>
	 * Longs.lastIndexOf(null, 1)      = -1
	 * Longs.lastIndexOf([1, 2, 1], 1) = 2
	 * </pre>
	 */
	public static int lastIndexOf(long[] a, long b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(long[] a, long b, int start) {
		if (a == null || start < 0) {
			return INDEX_NOT_FOUND;
		}
		for (int i = Math.min(start, a.length - 1); i >= 0; i--) {
			if (a[i] == b) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Returns a new array containing the elements of {@code left} followed by those of {@code right}.
	 *
	 * @throws IllegalArgumentException if either array is {@code null}
	 */
	public static long[] concat(long[] left, long[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		long[] longs = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, longs, left.length, right.length);
		return longs;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] add(long[] array, long a) {
		Assert.isNull(array, "Source array must not be null.");
		long[] longs = copy(array, array.length + 1);
		longs[longs.length - 1] = a;
		return longs;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static long[] remove(long[] array, long a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] removeAt(long[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			long[] target = create(length - 1, 0);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static long[] copy(long[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static long[] copy(long[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static long[] copy(long[] array, int startIndex, int length) {
		return copy(array, startIndex, length, 0);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static long[] copy(long[] array, int startIndex, int length, long defaultValue) {
		long[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(long[] src, int srcFrom, long[] dest, int destFrom, int length) {
		int n = Math.min(Math.min(src.length - srcFrom, dest.length - destFrom), length);
		if (n > 0) {
			System.arraycopy(src, srcFrom, dest, destFrom, n);
		}
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Long[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Long.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(Number[] array, Long defaultValue) {
		Long[] result = new Long[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Long[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Long[] toWrappers(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(Boolean[] value) {
		return valueOf(value, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(Boolean[] value, Long defaultValue) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = valueOf(value[i], defaultValue);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(Character[] value) {
		return valueOf(value, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(Character[] value, Long defaultValue) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = valueOf(value[i], defaultValue);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Long[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Long.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Long[] toWrappers(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Long.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Long[] toWrappers(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Long[] result = new Long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Long.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] toPrimitives(Byte[] array) {
		return toPrimitives(array, true);
	}

	/**
	 * Converts a wrapper array to a primitive array. {@code null} elements are skipped,
	 * or cause an {@code IllegalArgumentException} if {@code thrown} is true.
	 */
	public static long[] toPrimitives(Byte[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		long[] results = new long[array.length];
		int i = 0;
		for (Byte arg : array) {
			if (arg != null) {
				results[i++] = arg.longValue();
			} else if (thrown) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] toPrimitives(Short[] array) {
		return toPrimitives(array, true);
	}

	/**
	 * Converts a wrapper array to a primitive array. {@code null} elements are skipped,
	 * or cause an {@code IllegalArgumentException} if {@code thrown} is true.
	 */
	public static long[] toPrimitives(Short[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		long[] results = new long[array.length];
		int i = 0;
		for (Short arg : array) {
			if (arg != null) {
				results[i++] = arg.longValue();
			} else if (thrown) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] toPrimitives(Character[] array) {
		return toPrimitives(array, true);
	}

	/**
	 * Converts a wrapper array to a primitive array. {@code null} elements are skipped,
	 * or cause an {@code IllegalArgumentException} if {@code thrown} is true.
	 */
	public static long[] toPrimitives(Character[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		long[] results = new long[array.length];
		int i = 0;
		for (Character arg : array) {
			if (arg != null) {
				results[i++] = arg.charValue();
			} else if (thrown) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] toPrimitives(Integer[] array) {
		return toPrimitives(array, true);
	}

	/**
	 * Converts a wrapper array to a primitive array. {@code null} elements are skipped,
	 * or cause an {@code IllegalArgumentException} if {@code thrown} is true.
	 */
	public static long[] toPrimitives(Integer[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		long[] results = new long[array.length];
		int i = 0;
		for (Integer arg : array) {
			if (arg != null) {
				results[i++] = arg.longValue();
			} else if (thrown) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static long[] ensureCapacity(long[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static long[] expandCapacity(long[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, index, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static long[] expandCapacity(long[] array, int index, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	/**
	 * Returns the maximum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static long max(long[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		long max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max >= array[i] ? max : array[i];
		}
		return max;
	}

	/**
	 * Returns the minimum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static long min(long[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		long min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min <= array[i] ? min : array[i];
		}
		return min;
	}

	/**
	 * Returns the sum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static long sum(long[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		long sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/**
	 * Returns the arithmetic mean of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static double avg(long[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	/**
	 * Returns the elements joined by commas and enclosed in square brackets.
	 * <pre>
	 * Longs.toString(null)   = "[]"
	 * Longs.toString([1, 2]) = "[1,2]"
	 * </pre>
	 */
	public static String toString(long[] array) {
		return "[" + join(array) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 * <pre>
	 * Longs.join(null)           = ""
	 * Longs.join([1, 2], "|")   = "1|2"
	 * </pre>
	 */
	public static String join(long[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 * <pre>
	 * Longs.join(null)           = ""
	 * Longs.join([1, 2], "|")   = "1|2"
	 * </pre>
	 */
	public static String join(long[] array, String delimiter) {
		if (isEmpty(array)) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder str = new StringBuilder();
		for (int i = 0, l = array.length; i < l; i++) {
			str.append(array[i]);
			if (i != l - 1) {
				str.append(delimiter);
			}
		}
		return str.toString();
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 * <pre>
	 * Longs.join([1, 2], [3, 4], "=", ",") = "1=3,2=4"
	 * </pre>
	 */
	public static String join(long[] left, long[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 * <pre>
	 * Longs.join([1, 2], [3, 4], "=", ",") = "1=3,2=4"
	 * </pre>
	 */
	public static String join(long[] left, long[] right, String conjunction, String delimiter) {
		if (isEmpty(left) || isEmpty(right)) {
			return "";
		}
		if (conjunction == null) {
			conjunction = "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		for (int i = 0, l = Math.min(left.length, right.length); i < l; i++) {
			content.append(left[i]).append(conjunction).append(right[i]);
			if (i != l - 1) {
				content.append(delimiter);
			}
		}
		return content.toString();
	}

	/**
	 * Swaps the elements at the two indices.
	 */
	public static void swap(long[] x, int a, int b) {
		long t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(long[] array, int low, int high, boolean asc) {
		for (int i = low; i < high; i++) {
			for (int j = i; j > low; j--) {
				if (asc ? array[j - 1] > array[j] : array[j - 1] < array[j]) {
					swap(array, j - 1, j);
				}
			}
		}
	}

	/**
	 * Sorts the array in place (ascending or descending) using a stable merge sort; does nothing for {@code null} or empty arrays.
	 */
	public static void sort(long[] array, boolean asc) {
		if (isNotEmpty(array)) {
			long[] aux = (long[]) array.clone();
			mergeSort(aux, array, 0, array.length, asc);
		}
	}

	private static void mergeSort(long[] src, long[] dest, int low, int high, boolean asc) {
		int length = high - low;
		if (length < MERGE_SORT_THRESHOLD) {
			sort(dest, low, high, asc);
			return;
		}
		int mid = (high + low) / 2;
		mergeSort(dest, src, low, mid, asc);
		mergeSort(dest, src, mid, high, asc);
		int i = low;
		int p = low;
		int q = mid;
		while (p < mid && q < high) {
			if (asc ? src[p] <= src[q] : src[p] > src[q]) {
				dest[i++] = src[p++];
			} else {
				dest[i++] = src[q++];
			}
		}
		while (p < mid && i < high) {
			dest[i++] = src[p++];
		}
		while (q < high && i < high) {
			dest[i++] = src[q++];
		}
	}

	/**
	 * Concatenates two arrays keeping duplicates. If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Longs.unionAll([1, 2], [2, 3]) = [1, 2, 2, 3]
	 * Longs.unionAll(null, [1])      = [1]
	 * </pre>
	 */
	public static long[] unionAll(long[] left, long[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		long[] total = new long[left.length + right.length];
		int i = 0;
		for (long s : left) {
			total[i++] = s;
		}
		for (long s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Longs.union([0, 1, 1], [1, 2]) = [0, 1, 2]
	 * Longs.union(null, [1])         = [1]
	 * </pre>
	 */
	public static long[] union(long[] left, long[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		long[] total = new long[left.length + right.length];
		int i = 0;
		for (long s : left) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		for (long s : right) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the elements of {@code left} that are not contained in {@code right}.
	 * If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Longs.minus([1, 2, 3], [2]) = [1, 3]
	 * Longs.minus([1], null)      = [1]
	 * </pre>
	 */
	public static long[] minus(long[] left, long[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		long[] result = new long[left.length];
		int i = 0;
		for (long s : left) {
			if (!contains(right, s)) {
				result[i++] = s;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns the elements of {@code left} that are also contained in {@code right}.
	 * If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Longs.intersect([1, 2, 2], [2, 3]) = [2, 2]
	 * </pre>
	 */
	public static long[] intersect(long[] left, long[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		long[] result = new long[left.length];
		int i = 0;
		for (long s : left) {
			if (contains(right, s)) {
				result[i++] = s;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Converts a collection of numbers to a primitive array; elements that are not numbers become zero.
	 *
	 * @throws IllegalArgumentException if the collection is {@code null}
	 */
	public static long[] toArray(Collection<?> collection) {
		Assert.isNull(collection, "Source collection must not be null.");
		long[] array = new long[collection.size()];
		int i = 0;
		for (Object a : collection) {
			try {
				array[i++] = ((Number) a).longValue();
			} catch (RuntimeException e) {
			}
		}
		return ensureCapacity(array, i);
	}

	/**
	 * Converts the array to a new mutable list of wrappers.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static List<Long> toList(long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Long> set = new ArrayList<Long>(array.length);
		for (long a : array) {
			set.add(Long.valueOf(a));
		}
		return set;
	}

	/**
	 * Reverses the array in place; does nothing for {@code null}.
	 */
	public static void reverse(long[] src) {
		if (src == null) {
			return;
		}
		int j;
		long t, a;
		for (int i = 0, l = src.length; i < l / 2; i++) {
			t = src[i];
			j = l - 1 - i;
			a = src[j];
			src[i] = a;
			src[j] = t;
		}
	}

	/**
	 * Returns the hash code of a single value, consistent with the wrapper type.
	 */
	public static int hashCode(long arg) {
		return Long.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(long[] left, long[] right) {
		if (left == right) {
			return true;
		}
		if (left == null) {
			return right == null;
		} else if (right == null) {
			return false;
		}
		int length = left.length;
		if (length != right.length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (left[i] != right[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 * <pre>
	 * Longs.valueOf("12")      = 12
	 * Longs.valueOf("abc")     = null
	 * Longs.valueOf("", -1)    = -1
	 * </pre>
	 */
	public static Long valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 * <pre>
	 * Longs.valueOf("12")      = 12
	 * Longs.valueOf("abc")     = null
	 * Longs.valueOf("", -1)    = -1
	 * </pre>
	 */
	public static Long valueOf(String str, Long defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Long.valueOf(parse(str));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Long[] valueOf(String[] strings, Long defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Long[] result = new Long[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are skipped instead of causing an exception.
	 */
	public static long[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are skipped instead of causing an exception.
	 */
	public static long[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		long[] result = new long[strings.length];
		int i = 0;
		long s;
		for (String str : strings) {
			try {
				s = parse(str);
				result[i++] = s;
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Parses the string leniently. Besides plain numbers it accepts a leading '+', a leading '.', type suffixes
	 * (L/D/F), exponents and hexadecimal ({@code 0x}/{@code #}); fractions are truncated.
	 * Results are cached.
	 * <pre>
	 * Longs.parse("12")   = 12
	 * Longs.parse("0x1F") = 31
	 * Longs.parse("1.9")  = 1
	 * Longs.parse("12L")  = 12
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string is blank, not a number or out of range
	 */
	public static long parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
		}
		if (NumberUtils.isHex(str)) {
			try {
				return Long.decode(str);
			} catch (NumberFormatException e) {
			}
		}
		return parseStrictly(str);
	}

	private static long parseStrictly(String str) {
		Long pooled = cache.get(str);
		if (pooled != null) {
			return pooled.longValue();
		}
		String newStr;
		try {
			newStr = NumberUtils.read(str);
		} catch (RuntimeException e) {
			throw new NumberFormatException("Can not parse string for: " + str);
		}
		if (!NumberUtils.isNumber(newStr)) {
			throw new NumberFormatException("Can not parse string for: " + str);
		}
		long value;
		try {
			value = Long.parseLong(newStr);
		} catch (NumberFormatException e) {
			value = cast(new BigDecimal(newStr));
		}
		cache.put(str, value);
		return value;
	}

	/**
	 * Converts the value to {@code long}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static long cast(Number value) {
		if (value instanceof BigDecimal) {
			return cast((BigDecimal) value);
		} else if (value instanceof BigInteger) {
			return cast((BigInteger) value);
		} else {
			return value.longValue();
		}
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(Number[] array) {
		return casts(array, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		long[] result = new long[array.length];
		int i = 0;
		for (Number n : array) {
			try {
				result[i++] = cast(n);
			} catch (IllegalArgumentException | NullPointerException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Converts a boolean to 1/0.
	 */
	public static long cast(boolean b) {
		return (b ? 1 : 0);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts the value to {@code long}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static long cast(char value) {
		return (long) value;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		int i = 0;
		for (char val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(float[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(float[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		int i = 0;
		for (float val : value) {
			try {
				result[i++] = cast(val);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Converts the value to {@code long}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static long cast(BigInteger value) {
		if (NumberRangeAssert.checkLong(value)) {
			throw new NumberOverflowException(value);
		}
		return value.longValue();
	}

	/**
	 * Converts the value to {@code long}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static long cast(BigDecimal value) {
		return cast(value.toBigInteger());
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(double[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static long[] casts(double[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		long[] result = new long[value.length];
		int i = 0;
		for (double val : value) {
			try {
				result[i++] = cast(val);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Long valueOf(Number value) {
		return valueOf(value, null);
	}

	/**
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Long valueOf(Number value, Long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		try {
			return Long.valueOf(cast(value));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Long valueOf(Character c) {
		return valueOf(c, null);
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Long valueOf(Character c, Long defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return Long.valueOf(cast(c));
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Long valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Long valueOf(Boolean b, Long defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Long.valueOf(cast(b));
	}

	/**
	 * Returns a content-based hash code of the array, or {@code 0} for a {@code null} array.
	 */
	public static int deepHashCode(long[] args) {
		if (isEmpty(args)) {
			return 0;
		}
		int prime = 31;
		int hash = 1;
		for (int i = 0; i < args.length; i++) {
			hash = prime * hash + hashCode(args[i]);
		}
		return hash;
	}

	/**
	 * Checks if the value is even.
	 */
	public static boolean isEven(long value) {
		return (value & 1) == 0;
	}

	/**
	 * Checks if all values are even; returns {@code false} for a {@code null} array.
	 */
	public static boolean isEvens(long[] values) {
		if (values == null) {
			return false;
		}
		for (long l : values) {
			if (isOdd(l)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all values are odd; returns {@code false} for a {@code null} array.
	 */
	public static boolean isOdds(long[] values) {
		if (values == null) {
			return false;
		}
		for (long l : values) {
			if (isEven(l)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the value is odd.
	 */
	public static boolean isOdd(long value) {
		return !isEven(value);
	}

	/**
	 * Converts each element to a string using the given format (if not null); returns {@code null} for a {@code null} array.
	 */
	public static String[] toStringArray(long[] args, DecimalFormat df) {
		if (args == null) {
			return null;
		}
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = df != null ? df.format(args[i]) : String.valueOf(args[i]);
		}
		return array;
	}

	/**
	 * Checks if two arrays have the same length, treating {@code null} as an empty array.
	 */
	public static boolean isSameLength(long[] left, long[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 * <pre>
	 * Longs.isSerial([1, 2, 3])    = true
	 * Longs.isSerial([1, 3, 5], 2) = true
	 * </pre>
	 */
	public static boolean isSerial(long[] array) {
		return isSerial(array, 1);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 * <pre>
	 * Longs.isSerial([1, 2, 3])    = true
	 * Longs.isSerial([1, 3, 5], 2) = true
	 * </pre>
	 */
	public static boolean isSerial(long[] array, int n) {
		if (isEmpty(array)) {
			return false;
		}
		for (int i = array.length - 1; i > 0; i--) {
			if (array[i] - array[i - 1] != n) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if {@code right} occurs as a contiguous run inside {@code left}; returns {@code false} if either is {@code null} or empty.
	 * <pre>
	 * Longs.isSubarray([1, 2, 3, 4], [2, 3]) = true
	 * Longs.isSubarray([1, 2, 3], [1, 3])    = false
	 * </pre>
	 */
	public static boolean isSubarray(long[] left, long[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (int i = 0; i < left.length; i++) {
			if (left[i] == right[0]) {
				boolean f = true;
				for (int j = 1; j < right.length; j++) {
					if (i + j == left.length) {
						return false;
					}
					if (left[i + j] != right[j]) {
						f = false;
						break;
					}
				}
				if (f) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if every element of {@code right} is contained in {@code left}; returns {@code false} if either is {@code null} or empty.
	 * <pre>
	 * Longs.containsAll([1, 2, 3], [3, 1]) = true
	 * Longs.containsAll([1, 2], [1, 1, 1]) = true
	 * Longs.containsAll([1, 2], [4])       = false
	 * </pre>
	 */
	public static boolean containsAll(long[] left, long[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (long l : right) {
			if (notContains(left, l)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all elements are equal; returns {@code false} for a {@code null} or empty array.
	 */
	public static boolean same(long[] array) {
		return isSerial(array, 0);
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 * <pre>
	 * Longs.leftScroll([1, 2, 3, 4, 5], 2) -> [3, 4, 5, 1, 2]
	 * </pre>
	 */
	public static void leftScroll(long[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n % l);
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 * <pre>
	 * Longs.rightScroll([1, 2, 3, 4, 5], 2) -> [4, 5, 1, 2, 3]
	 * </pre>
	 */
	public static void rightScroll(long[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(long[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(long[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(long[] src, Random rn) {
		if (src == null) {
			return;
		}
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	/**
	 * Compares two values; the result is negative, zero or positive.
	 */
	public static int compare(long a, long b) {
		return (a < b) ? -1 : ((a > b) ? 1 : 0);
	}

	/**
	 * Returns a comparator that compares arrays lexicographically, element by element, then by length.
	 */
	public static Comparator<long[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	private static enum LexicographicalComparator implements Comparator<long[]> {
		INSTANCE;

		public int compare(long[] left, long[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Longs.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}

	/**
	 * Returns the two's complement binary representation padded with leading zeros to the full bit width.
	 */
	public static String toBinaryString(long number) {
		char[] chs = new char[Long.SIZE];
		for (int i = 0, l = Long.SIZE; i < l; i++) {
			chs[l - 1 - i] = (char) ((number >> i & 1) + '0');
		}
		return new String(chs);
	}

	/**
	 * Parses a binary string where a leading '1' is treated as the sign bit (two's complement).
	 * <pre>
	 * Longs.parseBinaryString("0101") = 5
	 * Longs.parseBinaryString("101")  = -3
	 * </pre>
	 */
	public static long parseBinaryString(String binaryString) {
		if (binaryString.charAt(0) == '0') {
			return Long.parseLong(binaryString, 2);
		}
		StringBuilder str = new StringBuilder();
		for (char bit : binaryString.toCharArray()) {
			str.append((bit == '0') ? 1 : 0);
		}
		long convertedLong = Long.parseLong(str.toString(), 2);
		return -(convertedLong + 1);
	}

	/**
	 * Equivalent of the {@code <<} operator implemented on the binary string representation.
	 */
	public static long shiftLeft(long number, int amount) {
		String original = toBinaryString(number);
		StringBuilder dest = new StringBuilder();
		amount &= 63;
		dest.append(original.toCharArray(), amount, original.length() - amount);
		for (int i = 0; i < amount; i++) {
			dest.append(0);
		}
		return parseBinaryString(dest.toString());
	}

	/**
	 * Equivalent of the {@code >>} operator implemented on the binary string representation.
	 */
	public static long shiftRight(long number, int amount) {
		String original = toBinaryString(number);
		StringBuilder dest = new StringBuilder();
		amount &= 63;
		dest.append(original.toCharArray(), 0, original.length() - amount);
		for (int i = 0; i < amount; i++) {
			dest.insert(0, number < 0 ? 1 : 0);
		}
		return parseBinaryString(dest.toString());
	}

	/**
	 * Equivalent of the {@code >>>} operator implemented on the binary string representation.
	 */
	public static long unsignedShiftRight(long number, int amount) {
		String original = toBinaryString(number);
		StringBuilder dest = new StringBuilder();
		amount &= 63;
		dest.append(original.toCharArray(), 0, original.length() - amount);
		for (int i = 0; i < amount; i++) {
			dest.insert(0, 0);
		}
		return parseBinaryString(dest.toString());
	}

	/**
	 * Equivalent of the {@code &} operator implemented on the binary string representation.
	 */
	public static long and(long left, long right) {
		String leftOrig = toBinaryString(left);
		String rightOrig = toBinaryString(right);
		char[] leftArray = leftOrig.toCharArray();
		char[] rightArray = rightOrig.toCharArray();
		char[] array = new char[leftOrig.length()];
		for (int i = 0; i < array.length; i++) {
			array[i] = leftArray[i] == '1' && rightArray[i] == '1' ? '1' : '0';
		}
		String binaryString = new String(array);
		return parseBinaryString(binaryString);
	}

	/**
	 * Equivalent of the {@code |} operator implemented on the binary string representation.
	 */
	public static long or(long left, long right) {
		String leftOrig = toBinaryString(left);
		String rightOrig = toBinaryString(right);
		char[] leftArray = leftOrig.toCharArray();
		char[] rightArray = rightOrig.toCharArray();
		char[] array = new char[leftOrig.length()];
		for (int i = 0; i < array.length; i++) {
			array[i] = leftArray[i] == '1' || rightArray[i] == '1' ? '1' : '0';
		}
		String binaryString = new String(array);
		return parseBinaryString(binaryString);
	}

	/**
	 * Equivalent of the {@code ^} operator implemented on the binary string representation.
	 */
	public static long xor(long left, long right) {
		String leftOrig = toBinaryString(left);
		String rightOrig = toBinaryString(right);
		char[] leftArray = leftOrig.toCharArray();
		char[] rightArray = rightOrig.toCharArray();
		char[] array = new char[leftOrig.length()];
		for (int i = 0; i < array.length; i++) {
			array[i] = leftArray[i] == rightArray[i] ? '0' : '1';
		}
		String binaryString = new String(array);
		return parseBinaryString(binaryString);
	}

	/**
	 * Equivalent of the {@code ~} operator implemented on the binary string representation.
	 */
	public static long not(long number) {
		String orig = toBinaryString(number);
		char[] leftArray = orig.toCharArray();
		char[] array = new char[orig.length()];
		for (int i = 0; i < array.length; i++) {
			array[i] = leftArray[i] == '1' ? '0' : '1';
		}
		String binaryString = new String(array);
		return parseBinaryString(binaryString);
	}
}
