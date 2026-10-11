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
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Null-safe utility methods for {@code double} values and arrays: searching, copying, sorting, set operations,
 * lenient parsing, rounding and range checks.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Doubles {

	public static final double[] EMPTY_ARRAY = new double[0];

	public static final Double[] EMPTY_OBJECT_ARRAY = new Double[0];

	public static final int BYTES = Double.SIZE / Byte.SIZE;

	private static final LruMap<String, Double> cache = new LruMap<String, Double>(128);

	/**
	 * Clears the internal cache of parsed strings.
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static double[] clone(double[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(double[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Checks if the value is neither infinite nor {@code NaN}.
	 */
	public static boolean isFinite(double value) {
		return NEGATIVE_INFINITY < value & value < POSITIVE_INFINITY;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static double[][] create(int row, int column) {
		return create(row, column, 0);
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static double[][] create(int row, int column, double defaultValue) {
		double[][] array = new double[row][];
		for (int i = 0; i < row; i++) {
			array[i] = create(column, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static double[] create(int length) {
		return create(length, 0d);
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static double[] create(int length, double defaultValue) {
		double[] array = new double[length];
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
	public static boolean isNotEmpty(double[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 * <pre>
	 * Doubles.isEmpty(null)   = true
	 * Doubles.isEmpty([])     = true
	 * Doubles.isEmpty([1])    = false
	 * </pre>
	 */
	public static boolean isEmpty(double[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(double[] a, double b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(double[] a, double b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 * <pre>
	 * Doubles.indexOf(null, 1)      = -1
	 * Doubles.indexOf([1, 2, 1], 1) = 0
	 * Doubles.indexOf([1, 2], 3)    = -1
	 * </pre>
	 */
	public static int indexOf(double[] a, double b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(double[] a, double b, int start, int end) {
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
	 * Doubles.lastIndexOf(null, 1)      = -1
	 * Doubles.lastIndexOf([1, 2, 1], 1) = 2
	 * </pre>
	 */
	public static int lastIndexOf(double[] a, double b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(double[] a, double b, int start) {
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
	public static double[] concat(double[] left, double[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		double[] doubles = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, doubles, left.length, right.length);
		return doubles;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] add(double[] array, double a) {
		Assert.isNull(array, "Source array must not be null.");
		double[] doubles = copy(array, array.length + 1);
		doubles[doubles.length - 1] = a;
		return doubles;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static double[] remove(double[] array, double a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] removeAt(double[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			double[] target = create(length - 1, 0d);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static double[] copy(double[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static double[] copy(double[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static double[] copy(double[] array, int startIndex, int length) {
		return copy(array, startIndex, length, 0);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static double[] copy(double[] array, int startIndex, int length, double defaultValue) {
		double[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(double[] src, int srcFrom, double[] dest, int destFrom, int length) {
		int n = Math.min(Math.min(src.length - srcFrom, dest.length - destFrom), length);
		if (n > 0) {
			System.arraycopy(src, srcFrom, dest, destFrom, n);
		}
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] toPrimitives(Byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		double[] results = new double[array.length];
		int i = 0;
		for (Byte arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.doubleValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] toPrimitives(Short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		double[] results = new double[array.length];
		int i = 0;
		for (Short arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.doubleValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] toPrimitives(Integer[] array) {
		Assert.isNull(array, "Source array must not be null.");
		double[] results = new double[array.length];
		int i = 0;
		for (Integer arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.doubleValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] toPrimitives(Long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		double[] results = new double[array.length];
		int i = 0;
		for (Long arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.doubleValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] toPrimitives(Float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		double[] results = new double[array.length];
		int i = 0;
		for (Float arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.doubleValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] toPrimitives(Double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		double[] results = new double[array.length];
		int i = 0;
		for (Double arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.doubleValue();
		}
		return results;
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static double[] ensureCapacity(double[] array, int index) {
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
	public static double[] expandCapacity(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static double[] expandCapacity(double[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	/**
	 * Returns the greater of the two values, ignoring {@code NaN} unless both are {@code NaN}.
	 */
	public static double max(double a, double b) {
		if (Double.isNaN(a)) {
			return b;
		} else if (Double.isNaN(b)) {
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	/**
	 * Returns the smaller of the two values, ignoring {@code NaN} unless both are {@code NaN}.
	 */
	public static double min(double a, double b) {
		if (Double.isNaN(a)) {
			return b;
		} else if (Double.isNaN(b)) {
			return a;
		} else {
			return Math.min(a, b);
		}
	}

	/**
	 * Returns the maximum of the array elements ({@code NaN} values are ignored).
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static double max(double[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		double max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max(max, array[i]);
		}
		return max;
	}

	/**
	 * Returns the minimum of the array elements ({@code NaN} values are ignored).
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static double min(double[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		double min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min(min, array[i]);
		}
		return min;
	}

	/**
	 * Returns the sum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static double sum(double[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		double sum = array[0];
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
	public static double avg(double[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	/**
	 * Returns the elements joined by commas and enclosed in square brackets.
	 */
	public static String toString(double[] array) {
		return "[" + join(array) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(double[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(double[] array, String delimiter) {
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
	 */
	public static String join(double[] left, double[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 */
	public static String join(double[] left, double[] right, String conjunction, String delimiter) {
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
	public static void swap(double[] x, int a, int b) {
		double t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(double[] array, int low, int high, boolean asc) {
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
	public static void sort(double[] array, boolean asc) {
		if (isNotEmpty(array)) {
			double[] aux = (double[]) array.clone();
			mergeSort(aux, array, 0, array.length, asc);
		}
	}

	private static void mergeSort(double[] src, double[] dest, int low, int high, boolean asc) {
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
	 * Doubles.unionAll([1, 2], [2, 3]) = [1, 2, 2, 3]
	 * Doubles.unionAll(null, [1])      = [1]
	 * </pre>
	 */
	public static double[] unionAll(double[] left, double[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		double[] total = new double[left.length + right.length];
		int i = 0;
		for (double s : left) {
			total[i++] = s;
		}
		for (double s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Doubles.union([0, 1, 1], [1, 2]) = [0, 1, 2]
	 * Doubles.union(null, [1])         = [1]
	 * </pre>
	 */
	public static double[] union(double[] left, double[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		double[] total = new double[left.length + right.length];
		int i = 0;
		for (double s : left) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		for (double s : right) {
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
	 * Doubles.minus([1, 2, 3], [2]) = [1, 3]
	 * Doubles.minus([1], null)      = [1]
	 * </pre>
	 */
	public static double[] minus(double[] left, double[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		double[] result = new double[left.length];
		int i = 0;
		for (double s : left) {
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
	 * Doubles.intersect([1, 2, 2], [2, 3]) = [2, 2]
	 * </pre>
	 */
	public static double[] intersect(double[] left, double[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		double[] result = new double[left.length];
		int i = 0;
		for (double s : left) {
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
	public static double[] toArray(Collection<?> collection) {
		Assert.isNull(collection, "Source collection list must not be null.");
		double[] array = new double[collection.size()];
		int i = 0;
		for (Object a : collection) {
			try {
				array[i++] = ((Number) a).doubleValue();
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
	public static List<Double> toList(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Double> set = new ArrayList<Double>(array.length);
		for (double a : array) {
			set.add(Double.valueOf(a));
		}
		return set;
	}

	/**
	 * Reverses the array in place; does nothing for {@code null}.
	 */
	public static void reverse(double[] src) {
		if (src == null) {
			return;
		}
		int j;
		double t, a;
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
	public static int hashCode(double arg) {
		return Double.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(double[] left, double[] right) {
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
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Double valueOf(Character c) {
		return valueOf(c, null);
	}

	/**
	 * Converts the character code to a number, returning the default value (null if absent) for {@code null}
	 * or a code that does not fit.
	 */
	public static Double valueOf(Character c, Double defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return Double.valueOf(c.charValue());
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Double valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Double valueOf(Boolean b, Double defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Double.valueOf(cast(b));
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 */
	public static Double valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 */
	public static Double valueOf(String str, Double defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Double.valueOf(parse(str));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Double[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Double[] valueOf(String[] strings, Double defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Double[] result = new Double[strings.length];
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
	public static double[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are skipped instead of causing an exception.
	 */
	public static double[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		double[] result = new double[strings.length];
		int i = 0;
		double s;
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
	 * (L/D/F) and exponents. Results are cached.
	 *
	 * @throws IllegalArgumentException if the string is blank or not a number
	 */
	public static double parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
		}
		return parseStrictly(str);
	}

	private static double parseStrictly(String str) {
		Double pooled = cache.get(str);
		if (pooled != null) {
			return pooled.doubleValue();
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
		double value = Double.parseDouble(newStr);
		cache.put(str, value);
		return value;
	}

	/**
	 * Converts a boolean to 1/0.
	 */
	public static double cast(boolean b) {
		return (b ? 1D : 0D);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts the value to {@code double}.
	 */
	public static double cast(char value) {
		return (double) value;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		int i = 0;
		for (char val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts the value to {@code double}.
	 */
	public static double cast(Number n) {
		Assert.isNull(n, "Number is required.");
		return n.doubleValue();
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(Number[] array) {
		return casts(array, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		double[] result = new double[array.length];
		int i = 0;
		for (Number n : array) {
			try {
				result[i++] = cast(n);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(float[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(double[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Double[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static double[] casts(float[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Double valueOf(Number n) {
		return valueOf(n, null);
	}

	/**
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Double valueOf(Number n, Double defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		try {
			return Double.valueOf(cast(n));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Double[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Double[] valueOf(Number[] array, Double defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Double[] result = new Double[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	/**
	 * Returns a content-based hash code of the array, or {@code 0} for a {@code null} array.
	 */
	public static int deepHashCode(double[] args) {
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
	 * Rounds the value to {@code scale} decimal places using {@link RoundingMode#HALF_EVEN}; {@code NaN} and infinities are returned unchanged.
	 */
	public static double toFixed(double value, int scale) {
		return toFixed(value, scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * Rounds the value to {@code scale} decimal places using the given rounding mode; {@code NaN} and infinities are returned unchanged.
	 */
	public static double toFixed(double value, int scale, RoundingMode roundingMode) {
		if (!isFinite(value)) {
			return value;
		}
		return new BigDecimal(value).setScale(scale, roundingMode).doubleValue();
	}

	/**
	 * Returns the value without exponent notation (via {@link java.math.BigDecimal#valueOf(double)});
	 * {@code NaN} and infinities are returned as {@link String#valueOf}.
	 */
	public static String toPlainString(double value) {
		if (!isFinite(value)) {
			return String.valueOf(value);
		}
		return BigDecimal.valueOf(value).toPlainString();
	}

	/**
	 * Converts each element to a string using the given format (if not null); returns {@code null} for a {@code null} array.
	 */
	public static String[] toStringArray(double[] args, DecimalFormat df) {
		if (args == null) {
			return null;
		}
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = df != null ? df.format(args[i]) : toPlainString(args[i]);
		}
		return array;
	}

	/**
	 * Checks if two arrays have the same length, treating {@code null} as an empty array.
	 */
	public static boolean isSameLength(double[] left, double[] right) {
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
	public static boolean same(double[] array) {
		return isSerial(array, 0);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 */
	public static boolean isSerial(double[] array, double n) {
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
	 * Doubles.isSubarray([1, 2, 3, 4], [2, 3]) = true
	 * Doubles.isSubarray([1, 2, 3], [1, 3])    = false
	 * </pre>
	 */
	public static boolean isSubarray(double[] left, double[] right) {
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
	 * Doubles.containsAll([1, 2, 3], [3, 1]) = true
	 * Doubles.containsAll([1, 2], [1, 1, 1]) = true
	 * Doubles.containsAll([1, 2], [4])       = false
	 * </pre>
	 */
	public static boolean containsAll(double[] left, double[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (double d : right) {
			if (notContains(left, d)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 * <pre>
	 * Doubles.leftScroll([1, 2, 3, 4, 5], 2) -> [3, 4, 5, 1, 2]
	 * </pre>
	 */
	public static void leftScroll(double[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n % l);
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 * <pre>
	 * Doubles.rightScroll([1, 2, 3, 4, 5], 2) -> [4, 5, 1, 2, 3]
	 * </pre>
	 */
	public static void rightScroll(double[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(double[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Checks if the value lies within {@code [start, end]}.
	 */
	public static boolean between(double val, double start, double end) {
		return compare(val, start) >= 0 && compare(val, end) <= 0;
	}

	/**
	 * Checks if the value lies within {@code (start, end)}.
	 */
	public static boolean in(double val, double start, double end) {
		return compare(val, start) > 0 && compare(val, end) < 0;
	}

	/**
	 * Checks if the value lies within {@code [start, end)}.
	 */
	public static boolean rightIn(double val, double start, double end) {
		return compare(val, start) >= 0 && compare(val, end) < 0;
	}

	/**
	 * Checks if the value lies within {@code (start, end]}.
	 */
	public static boolean leftIn(double val, double start, double end) {
		return compare(val, start) > 0 && compare(val, end) <= 0;
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(double[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(double[] src, Random rn) {
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
	public static int compare(double a, double b) {
		return Double.compare(a, b);
	}

	/**
	 * Returns a comparator that compares arrays lexicographically, element by element, then by length.
	 */
	public static Comparator<double[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	private static enum LexicographicalComparator implements Comparator<double[]> {
		INSTANCE;

		public int compare(double[] left, double[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Doubles.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}
}
