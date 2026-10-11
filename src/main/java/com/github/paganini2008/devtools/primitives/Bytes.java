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
import java.nio.ByteBuffer;
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
 * Null-safe utility methods for {@code byte} values and arrays: searching, copying, sorting, set operations,
 * lenient parsing, range-checked casting and big-endian conversion between primitives and byte arrays.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Bytes {

	public static final byte[] EMPTY_ARRAY = new byte[0];

	public static final Byte[] EMPTY_OBJECT_ARRAY = new Byte[0];

	private static final LruMap<String, Byte> cache = new LruMap<String, Byte>(128);

	/**
	 * Clears the internal cache of parsed strings.
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static byte[] clone(byte[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(byte[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static byte[][] create(int yLength, int xLength, byte defaultValue) {
		byte[][] array = new byte[yLength][];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static byte[] create(int length, byte defaultValue) {
		byte[] array = new byte[length];
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
	public static boolean isNotEmpty(byte[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 * <pre>
	 * Bytes.isEmpty(null)   = true
	 * Bytes.isEmpty([])     = true
	 * Bytes.isEmpty([1])    = false
	 * </pre>
	 */
	public static boolean isEmpty(byte[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(byte[] a, byte b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(byte[] a, byte b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 * <pre>
	 * Bytes.indexOf(null, 1)      = -1
	 * Bytes.indexOf([1, 2, 1], 1) = 0
	 * Bytes.indexOf([1, 2], 3)    = -1
	 * </pre>
	 */
	public static int indexOf(byte[] a, byte b) {
		return indexOf(a, b, 0);
	}

	/**
	 * Finds the index of the given value, starting the search at {@code start}. Returns {@code -1} if not found.
	 */
	public static int indexOf(byte[] a, byte b, int start) {
		return indexOf(a, b, start, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(byte[] a, byte b, int start, int end) {
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
	 * Bytes.lastIndexOf(null, 1)      = -1
	 * Bytes.lastIndexOf([1, 2, 1], 1) = 2
	 * </pre>
	 */
	public static int lastIndexOf(byte[] a, byte b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(byte[] a, byte b, int start) {
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
	public static byte[] concat(byte[] left, byte[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		byte[] bytes = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, bytes, left.length, right.length);
		return bytes;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] add(byte[] array, byte a) {
		Assert.isNull(array, "Source array must not be null.");
		byte[] bytes = copy(array, array.length + 1);
		bytes[bytes.length - 1] = a;
		return bytes;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static byte[] remove(byte[] array, byte a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] removeAt(byte[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			byte[] target = create(length - 1, (byte) 0);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static byte[] copy(byte[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static byte[] copy(byte[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static byte[] copy(byte[] array, int startIndex, int length) {
		return copy(array, startIndex, length, (byte) 0);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static byte[] copy(byte[] array, int startIndex, int length, byte defaultValue) {
		byte[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(byte[] src, int srcFrom, byte[] dest, int destFrom, int length) {
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
	public static Byte[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Byte[] result = new Byte[value.length];
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
	public static Byte[] toWrappers(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Byte[] result = new Byte[value.length];
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
	public static Byte[] toWrappers(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		Byte[] results = new Byte[l];
		int i = 0;
		for (byte arg : array) {
			results[i++] = Byte.valueOf(arg);
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] toPrimitives(Byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		byte[] results = new byte[l];
		int i = 0;
		for (Byte arg : array) {
			if (arg != null) {
				results[i++] = arg.byteValue();
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static byte[] ensureCapacity(byte[] array, int index) {
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
	public static byte[] expandCapacity(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static byte[] expandCapacity(byte[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	/**
	 * Returns the maximum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static int max(byte[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		int max = array[0];
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
	public static int min(byte[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		int min = array[0];
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
	public static int sum(byte[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		int sum = array[0];
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
	public static double avg(byte[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	/**
	 * Returns the elements joined by commas and enclosed in square brackets.
	 * <pre>
	 * Bytes.toString(null)   = "[]"
	 * Bytes.toString([1, 2]) = "[1,2]"
	 * </pre>
	 */
	public static String toString(byte[] array) {
		return "[" + join(array) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 * <pre>
	 * Bytes.join(null)           = ""
	 * Bytes.join([1, 2], "|")   = "1|2"
	 * </pre>
	 */
	public static String join(byte[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 * <pre>
	 * Bytes.join(null)           = ""
	 * Bytes.join([1, 2], "|")   = "1|2"
	 * </pre>
	 */
	public static String join(byte[] array, String delimiter) {
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
	 * Bytes.join([1, 2], [3, 4], "=", ",") = "1=3,2=4"
	 * </pre>
	 */
	public static String join(byte[] left, byte[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 * <pre>
	 * Bytes.join([1, 2], [3, 4], "=", ",") = "1=3,2=4"
	 * </pre>
	 */
	public static String join(byte[] left, byte[] right, String conjunction, String delimiter) {
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
	public static void swap(byte[] x, int a, int b) {
		byte t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(byte[] array, int low, int high, boolean asc) {
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
	public static void sort(byte[] array, boolean asc) {
		if (isNotEmpty(array)) {
			byte[] aux = (byte[]) array.clone();
			mergeSort(aux, array, 0, array.length, asc);
		}
	}

	private static void mergeSort(byte[] src, byte[] dest, int low, int high, boolean asc) {
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
	 * Bytes.unionAll([1, 2], [2, 3]) = [1, 2, 2, 3]
	 * Bytes.unionAll(null, [1])      = [1]
	 * </pre>
	 */
	public static byte[] unionAll(byte[] left, byte[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		byte[] total = new byte[left.length + right.length];
		int i = 0;
		for (byte s : left) {
			total[i++] = s;
		}
		for (byte s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Bytes.union([0, 1, 1], [1, 2]) = [0, 1, 2]
	 * Bytes.union(null, [1])         = [1]
	 * </pre>
	 */
	public static byte[] union(byte[] left, byte[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		byte[] total = new byte[left.length + right.length];
		int i = 0;
		for (byte s : left) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		for (byte s : right) {
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
	 * Bytes.minus([1, 2, 3], [2]) = [1, 3]
	 * Bytes.minus([1], null)      = [1]
	 * </pre>
	 */
	public static byte[] minus(byte[] left, byte[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		byte[] result = new byte[left.length];
		int i = 0;
		for (byte s : left) {
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
	 * Bytes.intersect([1, 2, 2], [2, 3]) = [2, 2]
	 * </pre>
	 */
	public static byte[] intersect(byte[] left, byte[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		byte[] result = new byte[left.length];
		int i = 0;
		for (byte s : left) {
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
	public static byte[] toArray(Collection<?> collection) {
		Assert.isNull(collection, "Source collection must not be null.");
		byte[] array = new byte[collection.size()];
		int i = 0;
		for (Object a : collection) {
			try {
				array[i++] = ((Number) a).byteValue();
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
	public static List<Byte> toList(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Byte> set = new ArrayList<Byte>(array.length);
		for (byte a : array) {
			set.add(Byte.valueOf(a));
		}
		return set;
	}

	/**
	 * Reverses the array in place; does nothing for {@code null}.
	 */
	public static void reverse(byte[] src) {
		if (src == null) {
			return;
		}
		int j;
		byte t, a;
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
	public static int hashCode(byte arg) {
		return Byte.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(byte[] left, byte[] right) {
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
	public static Byte valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Byte valueOf(Boolean b, Byte defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Byte.valueOf(cast(b));
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Byte valueOf(Character ch) {
		return valueOf(ch, null);
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Byte valueOf(Character ch, Byte defaultValue) {
		if (ch == null) {
			return defaultValue;
		}
		try {
			return Byte.valueOf(cast(ch));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 * <pre>
	 * Bytes.valueOf("12")      = 12
	 * Bytes.valueOf("abc")     = null
	 * Bytes.valueOf("", -1)    = -1
	 * </pre>
	 */
	public static Byte valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 * <pre>
	 * Bytes.valueOf("12")      = 12
	 * Bytes.valueOf("abc")     = null
	 * Bytes.valueOf("", -1)    = -1
	 * </pre>
	 */
	public static Byte valueOf(String str, Byte defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Byte.valueOf(parse(str));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Byte[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Byte[] valueOf(String[] strings, Byte defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Byte[] result = new Byte[strings.length];
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
	public static byte[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are skipped instead of causing an exception.
	 */
	public static byte[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		byte[] result = new byte[strings.length];
		int i = 0;
		byte s;
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
	 * Bytes.parse("12")   = 12
	 * Bytes.parse("0x1F") = 31
	 * Bytes.parse("1.9")  = 1
	 * Bytes.parse("12L")  = 12
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string is blank, not a number or out of range
	 */
	public static byte parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		try {
			return Byte.parseByte(str);
		} catch (NumberFormatException e) {
		}
		if (NumberUtils.isHex(str)) {
			try {
				return Byte.decode(str);
			} catch (NumberFormatException e) {
			}
		}
		return parseStrictly(str);
	}

	private static byte parseStrictly(String str) {
		Byte pooled = cache.get(str);
		if (pooled != null) {
			return pooled.byteValue();
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
		byte value;
		try {
			value = Byte.parseByte(newStr);
		} catch (NumberFormatException e) {
			value = cast(new BigDecimal(newStr));
		}
		cache.put(str, value);
		return value;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(int[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(int[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
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
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(short[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(short[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
		int i = 0;
		for (short val : value) {
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
	public static byte[] casts(long[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(long[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
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
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(float[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(float[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
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
	public static byte[] casts(double[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(double[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
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
	 * Converts a boolean to 1/0.
	 */
	public static byte cast(boolean b) {
		return (byte) (b ? 1 : 0);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(char[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(char[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		byte[] result = new byte[value.length];
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
		return ensureCapacity(result, i);
	}

	/**
	 * Converts the value to {@code byte}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static byte cast(char c) {
		byte b = (byte) c;
		if (b != c) {
			throw new NumberOverflowException();
		}
		return b;
	}

	/**
	 * Converts the value to {@code byte}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static byte cast(Number value) {
		if (value instanceof BigDecimal) {
			return cast((BigDecimal) value);
		} else if (value instanceof BigInteger) {
			return cast((BigInteger) value);
		} else {
			return cast(value.longValue());
		}
	}

	/**
	 * Converts the value to {@code byte}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static byte cast(long value) {
		byte converted = (byte) value;
		if (value != converted) {
			throw new NumberOverflowException(value);
		}
		return converted;
	}

	/**
	 * Converts the value to {@code byte}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static byte cast(BigDecimal value) {
		return cast(value.toBigInteger());
	}

	/**
	 * Converts the value to {@code byte}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static byte cast(BigInteger value) {
		if (NumberRangeAssert.checkByte(value)) {
			throw new NumberOverflowException(value);
		}
		return value.byteValue();
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(Number[] array) {
		return casts(array, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static byte[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		byte[] result = new byte[array.length];
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
	public static Byte valueOf(Number n) {
		return valueOf(n, null);
	}

	/**
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Byte valueOf(Number n, Byte defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		if (n instanceof Byte) {
			return (Byte) n;
		}
		try {
			return Byte.valueOf(cast(n));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Byte[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Byte[] valueOf(Number[] array, Byte defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Byte[] result = new Byte[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	/**
	 * Returns a content-based hash code of the array, or {@code 0} for a {@code null} array.
	 */
	public static int deepHashCode(byte[] args) {
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
	public static String[] toStringArray(byte[] args, DecimalFormat df) {
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
	public static boolean isEven(byte value) {
		return (value & 1) == 0;
	}

	/**
	 * Checks if all values are even; returns {@code false} for a {@code null} array.
	 */
	public static boolean isEvens(byte[] values) {
		if (values == null) {
			return false;
		}
		for (byte b : values) {
			if (isOdd(b)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all values are odd; returns {@code false} for a {@code null} array.
	 */
	public static boolean isOdds(byte[] values) {
		if (values == null) {
			return false;
		}
		for (byte b : values) {
			if (isEven(b)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the value is odd.
	 */
	public static boolean isOdd(byte value) {
		return !isEven(value);
	}

	/**
	 * Checks if two arrays have the same length, treating {@code null} as an empty array.
	 */
	public static boolean isSameLength(byte[] left, byte[] right) {
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
	public static boolean same(byte[] array) {
		return isSerial(array, 0);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 * <pre>
	 * Bytes.isSerial([1, 2, 3])    = true
	 * Bytes.isSerial([1, 3, 5], 2) = true
	 * </pre>
	 */
	public static boolean isSerial(byte[] array) {
		return isSerial(array, 1);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 * <pre>
	 * Bytes.isSerial([1, 2, 3])    = true
	 * Bytes.isSerial([1, 3, 5], 2) = true
	 * </pre>
	 */
	public static boolean isSerial(byte[] array, int n) {
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
	 * Bytes.isSubarray([1, 2, 3, 4], [2, 3]) = true
	 * Bytes.isSubarray([1, 2, 3], [1, 3])    = false
	 * </pre>
	 */
	public static boolean isSubarray(byte[] left, byte[] right) {
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
	 * Bytes.containsAll([1, 2, 3], [3, 1]) = true
	 * Bytes.containsAll([1, 2], [1, 1, 1]) = true
	 * Bytes.containsAll([1, 2], [4])       = false
	 * </pre>
	 */
	public static boolean containsAll(byte[] left, byte[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (byte b : right) {
			if (notContains(left, b)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 * <pre>
	 * Bytes.leftScroll([1, 2, 3, 4, 5], 2) -> [3, 4, 5, 1, 2]
	 * </pre>
	 */
	public static void leftScroll(byte[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n % l);
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 * <pre>
	 * Bytes.rightScroll([1, 2, 3, 4, 5], 2) -> [4, 5, 1, 2, 3]
	 * </pre>
	 */
	public static void rightScroll(byte[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(byte[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(byte[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(byte[] src, Random rn) {
		if (src == null) {
			return;
		}
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	/**
	 * Converts the value to a big-endian byte array of one byte (-1 for true, 0 for false).
	 */
	public static byte[] toBytes(boolean b) {
		return new byte[] { b ? (byte) -1 : (byte) 0 };
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static boolean toBoolean(byte[] b) {
		if (b.length != 1) {
			throw new IllegalArgumentException("Array's size is not 1.");
		}
		return b[0] != (byte) 0;
	}

	/**
	 * Converts the value to a big-endian byte array.
	 */
	public static byte[] toBytes(char c) {
		byte[] b = new byte[2];
		b[0] = (byte) (c >>> 8);
		b[1] = (byte) c;
		return b;
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static char toChar(byte[] b) {
		if (b.length != 2) {
			throw new IllegalArgumentException("Array's size is not 2.");
		}
		char c = (char) ((b[0] << 8) & 0xFF00L);
		c |= (char) (b[1] & 0xFFL);
		return c;
	}

	/**
	 * Converts the value to a big-endian byte array.
	 */
	public static byte[] toBytes(short s) {
		byte[] b = new byte[2];
		b[0] = (byte) (s >>> 8);
		b[1] = (byte) s;
		return b;
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static short toShort(byte[] b) {
		if (b.length != 2) {
			throw new IllegalArgumentException("Array's size is not 2.");
		}
		short s = (short) ((b[0] << 8) & 0xFF00);
		s |= b[1] & 0xFF;
		return s;
	}

	/**
	 * Converts the value to a big-endian byte array.
	 */
	public static byte[] toBytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >>> 24);
		b[1] = (byte) (i >>> 16);
		b[2] = (byte) (i >>> 8);
		b[3] = (byte) i;
		return b;
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static int toInt(byte[] b) {
		if (b.length != 4) {
			throw new IllegalArgumentException("Array's size is not 4.");
		}
		int i = (b[0] << 24) & 0xFF000000;
		i |= (b[1] << 16) & 0xFF0000;
		i |= (b[2] << 8) & 0xFF00;
		i |= b[3] & 0xFF;
		return i;
	}

	/**
	 * Converts the value to a big-endian byte array.
	 */
	public static byte[] toBytes(long l) {
		byte[] b = new byte[8];
		b[0] = (byte) (l >>> 56);
		b[1] = (byte) (l >>> 48);
		b[2] = (byte) (l >>> 40);
		b[3] = (byte) (l >>> 32);
		b[4] = (byte) (l >>> 24);
		b[5] = (byte) (l >>> 16);
		b[6] = (byte) (l >>> 8);
		b[7] = (byte) (l);
		return b;
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static long toLong(byte[] b) {
		if (b.length != 8) {
			throw new IllegalArgumentException("Array's size is not 8.");
		}
		long l = ((long) b[0] << 56) & 0xFF00000000000000L;
		l |= ((long) b[1] << 48) & 0xFF000000000000L;
		l |= ((long) b[2] << 40) & 0xFF0000000000L;
		l |= ((long) b[3] << 32) & 0xFF00000000L;
		l |= ((long) b[4] << 24) & 0xFF000000L;
		l |= ((long) b[5] << 16) & 0xFF0000L;
		l |= ((long) b[6] << 8) & 0xFF00L;
		l |= (long) b[7] & 0xFFL;
		return l;
	}

	/**
	 * Converts the value to a big-endian byte array.
	 */
	public static byte[] toBytes(double d) {
		return toBytes(Double.doubleToLongBits(d));
	}

	/**
	 * Converts the value to a big-endian byte array.
	 */
	public static byte[] toBytes(float f) {
		return toBytes(Float.floatToIntBits(f));
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static float toFloat(byte[] b) {
		return Float.intBitsToFloat(toInt(b));
	}

	/**
	 * Converts a big-endian byte array produced by {@code toBytes} back to the value.
	 *
	 * @throws IllegalArgumentException if the array does not have the expected length
	 */
	public static double toDouble(byte[] b) {
		return Double.longBitsToDouble(toLong(b));
	}

	/**
	 * Reads the remaining bytes of the buffer into a new array, or returns {@code defaultValue} (null if absent) for a {@code null} buffer.
	 */
	public static byte[] toByteArray(ByteBuffer bb) {
		return toByteArray(bb, null);
	}

	/**
	 * Reads the remaining bytes of the buffer into a new array, or returns {@code defaultValue} (null if absent) for a {@code null} buffer.
	 */
	public static byte[] toByteArray(ByteBuffer bb, byte[] defaultValue) {
		if (bb == null) {
			return defaultValue;
		}
		byte[] result = new byte[bb.remaining()];
		bb.get(result);
		return result;
	}

	/**
	 * Returns a comparator that compares arrays lexicographically, element by element, then by length.
	 */
	public static Comparator<byte[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	/**
	 * Compares two values; the result is negative, zero or positive.
	 */
	public static int compare(byte a, byte b) {
		return a - b;
	}

	private static enum LexicographicalComparator implements Comparator<byte[]> {

		INSTANCE;

		public int compare(byte[] left, byte[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Bytes.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}

}
