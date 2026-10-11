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
 * Null-safe utility methods for {@code short} values and arrays: searching, copying, sorting, set operations,
 * lenient parsing and range-checked casting.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Shorts {

	public static final short[] EMPTY_SHORT_ARRAY = new short[0];

	public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

	private static final LruMap<String, Short> cache = new LruMap<String, Short>(1024);

	public static final int BYTES = Short.SIZE / Byte.SIZE;

	public static final short MAX_POWER_OF_TWO = 1 << (Short.SIZE - 2);

	/**
	 * Clears the internal cache of parsed strings.
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static short[] clone(short[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(short[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static short[][] create(int yLength, int xLength, short defaultValue) {
		short[][] array = new short[yLength][];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static short[] create(int length, short defaultValue) {
		short[] array = new short[length];
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
	public static boolean isNotEmpty(short[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 * <pre>
	 * Shorts.isEmpty(null)   = true
	 * Shorts.isEmpty([])     = true
	 * Shorts.isEmpty([1])    = false
	 * </pre>
	 */
	public static boolean isEmpty(short[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(short[] a, short b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(short[] a, short b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 * <pre>
	 * Shorts.indexOf(null, 1)      = -1
	 * Shorts.indexOf([1, 2, 1], 1) = 0
	 * Shorts.indexOf([1, 2], 3)    = -1
	 * </pre>
	 */
	public static int indexOf(short[] a, short b) {
		return indexOf(a, b, 0);
	}

	/**
	 * Finds the index of the given value, starting the search at {@code start}. Returns {@code -1} if not found.
	 */
	public static int indexOf(short[] a, short b, int start) {
		return indexOf(a, b, start, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(short[] a, short b, int start, int end) {
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
	 * Shorts.lastIndexOf(null, 1)      = -1
	 * Shorts.lastIndexOf([1, 2, 1], 1) = 2
	 * </pre>
	 */
	public static int lastIndexOf(short[] a, short b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(short[] a, short b, int start) {
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
	public static short[] concat(short[] left, short[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		short[] shorts = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, shorts, left.length, right.length);
		return shorts;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] add(short[] array, short a) {
		Assert.isNull(array, "Source array must not be null.");
		short[] shorts = copy(array, array.length + 1);
		shorts[shorts.length - 1] = a;
		return shorts;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static short[] remove(short[] array, short a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] removeAt(short[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			short[] target = create(length - 1, (short) 0);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static short[] copy(short[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static short[] copy(short[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static short[] copy(short[] array, int startIndex, int length) {
		return copy(array, startIndex, length, (short) 0);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static short[] copy(short[] array, int startIndex, int length, short defaultValue) {
		short[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(short[] src, int srcFrom, short[] dest, int destFrom, int length) {
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
	public static Short[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Short[] result = new Short[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Short.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Short[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Short[] result = new Short[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Short.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] toPrimitives(Short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		short[] results = new short[l];
		int i = 0;
		for (Short arg : array) {
			if (arg != null) {
				results[i++] = arg.shortValue();
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static short[] ensureCapacity(short[] array, int index) {
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
	public static short[] expandCapacity(short[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, index, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static short[] expandCapacity(short[] array, int index, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	/**
	 * Returns the maximum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static short max(short[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		short max = array[0];
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
	public static short min(short[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		short min = array[0];
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
	public static short sum(short[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		short sum = array[0];
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
	public static double avg(short[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	/**
	 * Returns the elements joined by commas and enclosed in square brackets.
	 * <pre>
	 * Shorts.toString(null)   = "[]"
	 * Shorts.toString([1, 2]) = "[1,2]"
	 * </pre>
	 */
	public static String toString(short[] array) {
		return "[" + join(array) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 * <pre>
	 * Shorts.join(null)           = ""
	 * Shorts.join([1, 2], "|")   = "1|2"
	 * </pre>
	 */
	public static String join(short[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 * <pre>
	 * Shorts.join(null)           = ""
	 * Shorts.join([1, 2], "|")   = "1|2"
	 * </pre>
	 */
	public static String join(short[] array, String delimiter) {
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
	 * Shorts.join([1, 2], [3, 4], "=", ",") = "1=3,2=4"
	 * </pre>
	 */
	public static String join(short[] left, short[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 * <pre>
	 * Shorts.join([1, 2], [3, 4], "=", ",") = "1=3,2=4"
	 * </pre>
	 */
	public static String join(short[] left, short[] right, String conjunction, String delimiter) {
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
	public static void swap(short[] x, int a, int b) {
		short t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(short[] array, int low, int high, boolean asc) {
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
	public static void sort(short[] array, boolean asc) {
		if (isNotEmpty(array)) {
			short[] aux = (short[]) array.clone();
			mergeSort(aux, array, 0, array.length, asc);
		}
	}

	private static void mergeSort(short[] src, short[] dest, int low, int high, boolean asc) {
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
	 * Shorts.unionAll([1, 2], [2, 3]) = [1, 2, 2, 3]
	 * Shorts.unionAll(null, [1])      = [1]
	 * </pre>
	 */
	public static short[] unionAll(short[] left, short[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		short[] total = new short[left.length + right.length];
		int i = 0;
		for (short s : left) {
			total[i++] = s;
		}
		for (short s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Shorts.union([0, 1, 1], [1, 2]) = [0, 1, 2]
	 * Shorts.union(null, [1])         = [1]
	 * </pre>
	 */
	public static short[] union(short[] left, short[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		short[] total = new short[left.length + right.length];
		int i = 0;
		for (short s : left) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		for (short s : right) {
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
	 * Shorts.minus([1, 2, 3], [2]) = [1, 3]
	 * Shorts.minus([1], null)      = [1]
	 * </pre>
	 */
	public static short[] minus(short[] left, short[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		short[] result = new short[left.length];
		int i = 0;
		for (short s : left) {
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
	 * Shorts.intersect([1, 2, 2], [2, 3]) = [2, 2]
	 * </pre>
	 */
	public static short[] intersect(short[] left, short[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		short[] result = new short[left.length];
		int i = 0;
		for (short s : left) {
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
	public static short[] toArray(Collection<?> collection) {
		Assert.isNull(collection, "Source collection must not be null.");
		short[] array = new short[collection.size()];
		int i = 0;
		for (Object a : collection) {
			try {
				array[i++] = ((Number) a).shortValue();
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
	public static List<Short> toList(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Short> set = new ArrayList<Short>(array.length);
		for (short a : array) {
			set.add(Short.valueOf(a));
		}
		return set;
	}

	/**
	 * Reverses the array in place; does nothing for {@code null}.
	 */
	public static void reverse(short[] src) {
		if (src == null) {
			return;
		}
		int j;
		short t, a;
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
	public static int hashCode(short arg) {
		return Short.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(short[] left, short[] right) {
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
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Short valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Short valueOf(Boolean b, Short defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Short.valueOf(cast(b));
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Short valueOf(Character ch) {
		return valueOf(ch, null);
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Short valueOf(Character c, Short defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return Short.valueOf(cast(c));
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 * <pre>
	 * Shorts.valueOf("12")      = 12
	 * Shorts.valueOf("abc")     = null
	 * Shorts.valueOf("", -1)    = -1
	 * </pre>
	 */
	public static Short valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 * <pre>
	 * Shorts.valueOf("12")      = 12
	 * Shorts.valueOf("abc")     = null
	 * Shorts.valueOf("", -1)    = -1
	 * </pre>
	 */
	public static Short valueOf(String str, Short defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Short.valueOf(parse(str));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Short[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Short[] valueOf(String[] strings, Short defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Short[] result = new Short[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	/**
	 * Parses the string leniently. Besides plain numbers it accepts a leading '+', a leading '.', type suffixes
	 * (L/D/F), exponents and hexadecimal ({@code 0x}/{@code #}); fractions are truncated.
	 * Results are cached.
	 * <pre>
	 * Shorts.parse("12")   = 12
	 * Shorts.parse("0x1F") = 31
	 * Shorts.parse("1.9")  = 1
	 * Shorts.parse("12L")  = 12
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string is blank, not a number or out of range
	 */
	public static short parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		try {
			return Short.parseShort(str);
		} catch (NumberFormatException e) {
		}
		if (NumberUtils.isHex(str)) {
			try {
				return Short.decode(str);
			} catch (NumberFormatException e) {
			}
		}
		return parseStrictly(str);
	}

	private static short parseStrictly(String str) {
		Short pooled = cache.get(str);
		if (pooled != null) {
			return pooled.shortValue();
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
		short value;
		try {
			value = Short.parseShort(newStr);
		} catch (NumberFormatException e) {
			value = cast(new BigDecimal(newStr));
		}
		cache.put(str, value);
		return value;
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are skipped instead of causing an exception.
	 */
	public static short[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are skipped instead of causing an exception.
	 */
	public static short[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		short[] result = new short[strings.length];
		int i = 0;
		short s;
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
	 * Converts a boolean to 1/0.
	 */
	public static short cast(boolean b) {
		return (short) (b ? 1 : 0);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts the value to {@code short}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static short cast(char value) {
		short s = (short) value;
		if (s != value) {
			throw new NumberOverflowException();
		}
		return s;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(char[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(char[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
		int i = 0;
		for (char val : value) {
			try {
				result[i++] = cast(val);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
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
	public static short[] casts(int[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(int[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
		int i = 0;
		for (int val : value) {
			try {
				result[i++] = cast(val);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return result;
	}

	/**
	 * Converts the value to {@code short}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static short cast(Number value) {
		if (value instanceof BigDecimal) {
			return cast((BigDecimal) value);
		} else if (value instanceof BigInteger) {
			return cast((BigInteger) value);
		} else {
			return cast(value.longValue());
		}
	}

	/**
	 * Converts the value to {@code short}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static short cast(long value) {
		short converted = (short) value;
		if (value != converted) {
			throw new NumberOverflowException(value);
		}
		return converted;
	}

	/**
	 * Converts the value to {@code short}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static short cast(BigDecimal value) {
		return cast(value.toBigInteger());
	}

	/**
	 * Converts the value to {@code short}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static short cast(BigInteger value) {
		if (NumberRangeAssert.checkShort(value)) {
			throw new NumberOverflowException(value);
		}
		return value.shortValue();
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(long[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(long[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
		int i = 0;
		for (long val : value) {
			try {
				result[i++] = cast(val);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return result;
	}

	/**
	 * Converts the value to {@code short}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static short cast(float value) {
		return cast((double) value);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(float[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(float[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
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
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(double[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(double[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
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
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(Number[] array) {
		return casts(array, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static short[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		short[] result = new short[array.length];
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
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Short valueOf(Number n) {
		return valueOf(n, null);
	}

	/**
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Short valueOf(Number n, Short defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		if (n instanceof Short) {
			return (Short) n;
		}
		try {
			return Short.valueOf(cast(n));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Short[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Short[] valueOf(Number[] array, Short defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Short[] result = new Short[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	/**
	 * Returns a content-based hash code of the array, or {@code 0} for a {@code null} array.
	 */
	public static int deepHashCode(short[] args) {
		if (args == null) {
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
	 * Converts each element to a string using the given format (if not null); returns {@code null} for a {@code null} array.
	 */
	public static String[] toStringArray(short[] args, DecimalFormat df) {
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
	 * Checks if the value is even.
	 */
	public static boolean isEven(short value) {
		return (value & 1) == 0;
	}

	/**
	 * Checks if all values are even; returns {@code false} for a {@code null} array.
	 */
	public static boolean isEvens(short[] values) {
		if (values == null) {
			return false;
		}
		for (short s : values) {
			if (isOdd(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all values are odd; returns {@code false} for a {@code null} array.
	 */
	public static boolean isOdds(short[] values) {
		if (values == null) {
			return false;
		}
		for (short s : values) {
			if (isEven(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the value is odd.
	 */
	public static boolean isOdd(short value) {
		return !isEven(value);
	}

	/**
	 * Checks if two arrays have the same length, treating {@code null} as an empty array.
	 */
	public static boolean isSameLength(short[] left, short[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	/**
	 * Checks if all elements are equal; returns {@code false} for a {@code null} or empty array.
	 */
	public static boolean same(short[] array) {
		return isSerial(array, 0);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 * <pre>
	 * Shorts.isSerial([1, 2, 3])    = true
	 * Shorts.isSerial([1, 3, 5], 2) = true
	 * </pre>
	 */
	public static boolean isSerial(short[] array) {
		return isSerial(array, 1);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 * <pre>
	 * Shorts.isSerial([1, 2, 3])    = true
	 * Shorts.isSerial([1, 3, 5], 2) = true
	 * </pre>
	 */
	public static boolean isSerial(short[] array, int n) {
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
	 * Shorts.isSubarray([1, 2, 3, 4], [2, 3]) = true
	 * Shorts.isSubarray([1, 2, 3], [1, 3])    = false
	 * </pre>
	 */
	public static boolean isSubarray(short[] left, short[] right) {
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
	 * Shorts.containsAll([1, 2, 3], [3, 1]) = true
	 * Shorts.containsAll([1, 2], [1, 1, 1]) = true
	 * Shorts.containsAll([1, 2], [4])       = false
	 * </pre>
	 */
	public static boolean containsAll(short[] left, short[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (short s : right) {
			if (notContains(left, s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 * <pre>
	 * Shorts.leftScroll([1, 2, 3, 4, 5], 2) -> [3, 4, 5, 1, 2]
	 * </pre>
	 */
	public static void leftScroll(short[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n % l);
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 * <pre>
	 * Shorts.rightScroll([1, 2, 3, 4, 5], 2) -> [4, 5, 1, 2, 3]
	 * </pre>
	 */
	public static void rightScroll(short[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(short[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(short[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(short[] src, Random rn) {
		if (src == null) {
			return;
		}
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	/**
	 * Returns a comparator that compares arrays lexicographically, element by element, then by length.
	 */
	public static Comparator<short[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	/**
	 * Compares two values; the result is negative, zero or positive.
	 */
	public static int compare(short a, short b) {
		return a - b;
	}

	private static enum LexicographicalComparator implements Comparator<short[]> {

		INSTANCE;

		public int compare(short[] left, short[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Shorts.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}
}
