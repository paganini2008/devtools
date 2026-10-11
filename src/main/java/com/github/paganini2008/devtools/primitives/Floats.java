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
import static java.lang.Float.NEGATIVE_INFINITY;
import static java.lang.Float.POSITIVE_INFINITY;

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
 * Null-safe utility methods for {@code float} values and arrays: searching, copying, sorting, set operations,
 * lenient parsing, rounding and range checks.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * 
 * @since 2.0.1
 */
public abstract class Floats {

	public static final float[] EMPTY_ARRAY = new float[0];

	public static final Float[] EMPTY_OBJECT_ARRAY = new Float[0];

	private static final LruMap<String, Float> cache = new LruMap<String, Float>(128);

	public static final int BYTES = Float.SIZE / Byte.SIZE;

	/**
	 * Clears the internal cache of parsed strings.
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static float[] clone(float[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(float[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Checks if the value is neither infinite nor {@code NaN}.
	 */
	public static boolean isFinite(float value) {
		return NEGATIVE_INFINITY < value & value < POSITIVE_INFINITY;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static float[][] create(int row, int column) {
		return create(row, column, 0);
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static float[][] create(int row, int column, float defaultValue) {
		float[][] array = new float[row][];
		for (int i = 0; i < row; i++) {
			array[i] = create(column, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static float[] create(int length) {
		return create(length, 0f);
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static float[] create(int length, float defaultValue) {
		float[] array = new float[length];
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
	public static boolean isNotEmpty(float[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 * <pre>
	 * Floats.isEmpty(null)   = true
	 * Floats.isEmpty([])     = true
	 * Floats.isEmpty([1])    = false
	 * </pre>
	 */
	public static boolean isEmpty(float[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(float[] a, float b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(float[] a, float b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 * <pre>
	 * Floats.indexOf(null, 1)      = -1
	 * Floats.indexOf([1, 2, 1], 1) = 0
	 * Floats.indexOf([1, 2], 3)    = -1
	 * </pre>
	 */
	public static int indexOf(float[] a, float b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(float[] a, float b, int start, int end) {
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
	 * Floats.lastIndexOf(null, 1)      = -1
	 * Floats.lastIndexOf([1, 2, 1], 1) = 2
	 * </pre>
	 */
	public static int lastIndexOf(float[] a, float b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(float[] a, float b, int start) {
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
	public static float[] concat(float[] left, float[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		float[] floats = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, floats, left.length, right.length);
		return floats;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] add(float[] array, float a) {
		Assert.isNull(array, "Source array must not be null.");
		float[] floats = copy(array, array.length + 1);
		floats[floats.length - 1] = a;
		return floats;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static float[] remove(float[] array, float a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] removeAt(float[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			float[] target = create(length - 1, 0f);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static float[] copy(float[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static float[] copy(float[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static float[] copy(float[] array, int startIndex, int length) {
		return copy(array, startIndex, length, 0);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static float[] copy(float[] array, int startIndex, int length, float defaultValue) {
		float[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(float[] src, int srcFrom, float[] dest, int destFrom, int length) {
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
	public static float[] toPrimitives(Byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		float[] results = new float[array.length];
		int i = 0;
		for (Byte arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.floatValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] toPrimitives(Short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		float[] results = new float[array.length];
		int i = 0;
		for (Short arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.floatValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] toPrimitives(Character[] array) {
		Assert.isNull(array, "Source array must not be null.");
		float[] results = new float[array.length];
		int i = 0;
		for (Character arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.charValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] toPrimitives(Integer[] array) {
		Assert.isNull(array, "Source array must not be null.");
		float[] results = new float[array.length];
		int i = 0;
		for (Integer arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.floatValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] toPrimitives(Long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		float[] results = new float[array.length];
		int i = 0;
		for (Long arg : array) {
			if (arg == null) {
				throw new IllegalArgumentException("Null value in array. Index: " + i);
			}
			results[i++] = arg.floatValue();
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] toPrimitives(Float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		float[] results = new float[l];
		int i = 0;
		for (Float arg : array) {
			if (arg != null) {
				results[i++] = arg.floatValue();
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static float[] ensureCapacity(float[] array, int index) {
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
	public static float[] expandCapacity(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static float[] expandCapacity(float[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	/**
	 * Returns the maximum of the array elements ({@code NaN} values are ignored).
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static float max(float[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		float max = array[0];
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
	public static float min(float[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		float min = array[0];
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
	public static float sum(float[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		float sum = array[0];
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
	public static double avg(float[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	/**
	 * Returns the elements joined by commas and enclosed in square brackets.
	 */
	public static String toString(float[] array) {
		return "[" + join(array) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(float[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(float[] array, String delimiter) {
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
	public static String join(float[] left, float[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 */
	public static String join(float[] left, float[] right, String conjunction, String delimiter) {
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
	public static void swap(float[] x, int a, int b) {
		float t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(float[] array, int low, int high, boolean asc) {
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
	public static void sort(float[] array, boolean asc) {
		if (isNotEmpty(array)) {
			float[] aux = (float[]) array.clone();
			mergeSort(aux, array, 0, array.length, asc);
		}
	}

	private static void mergeSort(float[] src, float[] dest, int low, int high, boolean asc) {
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
	 * Floats.unionAll([1, 2], [2, 3]) = [1, 2, 2, 3]
	 * Floats.unionAll(null, [1])      = [1]
	 * </pre>
	 */
	public static float[] unionAll(float[] left, float[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		float[] total = new float[left.length + right.length];
		int i = 0;
		for (float s : left) {
			total[i++] = s;
		}
		for (float s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one array is {@code null} the other one is returned as is.
	 * <pre>
	 * Floats.union([0, 1, 1], [1, 2]) = [0, 1, 2]
	 * Floats.union(null, [1])         = [1]
	 * </pre>
	 */
	public static float[] union(float[] left, float[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		float[] total = new float[left.length + right.length];
		int i = 0;
		for (float s : left) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		for (float s : right) {
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
	 * Floats.minus([1, 2, 3], [2]) = [1, 3]
	 * Floats.minus([1], null)      = [1]
	 * </pre>
	 */
	public static float[] minus(float[] left, float[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		float[] result = new float[left.length];
		int i = 0;
		for (float s : left) {
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
	 * Floats.intersect([1, 2, 2], [2, 3]) = [2, 2]
	 * </pre>
	 */
	public static float[] intersect(float[] left, float[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		float[] result = new float[left.length];
		int i = 0;
		for (float s : left) {
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
	public static float[] toArray(Collection<?> collection) {
		Assert.isNull(collection, "Source collection must not be null.");
		float[] array = new float[collection.size()];
		int i = 0;
		for (Object a : collection) {
			try {
				array[i++] = ((Number) a).floatValue();
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
	public static List<Float> toList(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Float> set = new ArrayList<Float>(array.length);
		for (float a : array) {
			set.add(Float.valueOf(a));
		}
		return set;
	}

	/**
	 * Reverses the array in place; does nothing for {@code null}.
	 */
	public static void reverse(float[] src) {
		if (src == null) {
			return;
		}
		int j;
		float t, a;
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
	public static int hashCode(float arg) {
		return Float.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(float[] left, float[] right) {
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
	 * Parses the character as a digit (e.g. '5' gives 5.0), returning the default value (null if absent)
	 * for {@code null} or non-numeric characters.
	 */
	public static Float valueOf(Character c) {
		return valueOf(c, null);
	}

	/**
	 * Parses the character as a digit (e.g. '5' gives 5.0), returning the default value (null if absent)
	 * for {@code null} or non-numeric characters.
	 */
	public static Float valueOf(Character c, Float defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return valueOf(String.valueOf(c), defaultValue);
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Float valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true/false to 1/0, returning the default value (null if absent) for {@code null}.
	 */
	public static Float valueOf(Boolean b, Float defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Float.valueOf(cast(b));
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 */
	public static Float valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses the string leniently (see {@link #parse(String)}), returning the default value (null if absent)
	 * if it is blank or cannot be parsed.
	 */
	public static Float valueOf(String str, Float defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Float.valueOf(parse(str));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Float[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Float[] valueOf(String[] strings, Float defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Float[] result = new Float[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are left as zero instead of causing an exception.
	 */
	public static float[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are left as zero instead of causing an exception.
	 */
	public static float[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		float[] result = new float[strings.length];
		int i = 0;
		for (String str : strings) {
			try {
				result[i++] = parse(str);
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
	public static float parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
		}
		return parseStrictly(str);
	}

	private static float parseStrictly(String str) {
		Float pooled = cache.get(str);
		if (pooled != null) {
			return pooled.floatValue();
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
		float value = Float.parseFloat(newStr);
		cache.put(str, value);
		return value;
	}

	/**
	 * Converts a boolean to 1/0.
	 */
	public static float cast(boolean b) {
		return (b ? 1F : 0F);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts the value to {@code float}.
	 */
	public static float cast(char value) {
		return (float) value;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] casts(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
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
	public static float[] casts(double[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] casts(double[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
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
	 * Converts the value to {@code float}.
	 */
	public static float cast(Number n) {
		Assert.isNull(n, "Number is required.");
		return n.floatValue();
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] casts(Number[] array) {
		return casts(array, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		float[] result = new float[array.length];
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
	 * Converts the number (with range checking where applicable), returning the default value (null if absent)
	 * for {@code null} or values that do not fit.
	 */
	public static Float valueOf(Number n, Float defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		if (n instanceof Float) {
			return (Float) n;
		}
		try {
			return Float.valueOf(cast(n));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Float[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Float[] valueOf(Number[] array, Float defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Float[] result = new Float[array.length];
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
	public static Float[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Float[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Float[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Float[] toWrappers(float[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Float[] toWrappers(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts a primitive array to the corresponding wrapper array.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static Float[] toWrappers(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static float[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
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
	public static float[] casts(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
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
	public static float[] casts(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
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
	public static float[] casts(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	/**
	 * Returns a content-based hash code of the array, or {@code 0} for a {@code null} array.
	 */
	public static int deepHashCode(float[] args) {
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
	public static float toFixed(float value, int scale) {
		return toFixed(value, scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * Rounds the value to {@code scale} decimal places using the given rounding mode; {@code NaN} and infinities are returned unchanged.
	 */
	public static float toFixed(float value, int scale, RoundingMode roundingMode) {
		if (!isFinite(value)) {
			return value;
		}
		return new BigDecimal(value).setScale(scale, roundingMode).floatValue();
	}

	/**
	 * Returns the value without exponent notation (via {@link java.math.BigDecimal#valueOf(double)});
	 * {@code NaN} and infinities are returned as {@link String#valueOf}.
	 */
	public static String toPlainString(float value) {
		if (!isFinite(value)) {
			return String.valueOf(value);
		}
		return BigDecimal.valueOf(value).toPlainString();
	}

	/**
	 * Converts each element to a string using the given format (if not null); returns {@code null} for a {@code null} array.
	 */
	public static String[] toStringArray(float[] args, DecimalFormat df) {
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
	public static boolean isSameLength(float[] left, float[] right) {
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
	public static boolean same(float[] array) {
		return isSerial(array, 0);
	}

	/**
	 * Checks if each element differs from the previous one by exactly {@code n} (1 if absent);
	 * returns {@code false} for a {@code null} or empty array.
	 */
	public static boolean isSerial(float[] array, float n) {
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
	 * Floats.isSubarray([1, 2, 3, 4], [2, 3]) = true
	 * Floats.isSubarray([1, 2, 3], [1, 3])    = false
	 * </pre>
	 */
	public static boolean isSubarray(float[] left, float[] right) {
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
	 * Floats.containsAll([1, 2, 3], [3, 1]) = true
	 * Floats.containsAll([1, 2], [1, 1, 1]) = true
	 * Floats.containsAll([1, 2], [4])       = false
	 * </pre>
	 */
	public static boolean containsAll(float[] left, float[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (float f : right) {
			if (notContains(left, f)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 * <pre>
	 * Floats.leftScroll([1, 2, 3, 4, 5], 2) -> [3, 4, 5, 1, 2]
	 * </pre>
	 */
	public static void leftScroll(float[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n % l);
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 * <pre>
	 * Floats.rightScroll([1, 2, 3, 4, 5], 2) -> [4, 5, 1, 2, 3]
	 * </pre>
	 */
	public static void rightScroll(float[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(float[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Returns the greater of the two values, ignoring {@code NaN} unless both are {@code NaN}.
	 */
	public static float max(float a, float b) {
		if (Float.isNaN(a)) {
			return b;
		} else if (Float.isNaN(b)) {
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	/**
	 * Returns the smaller of the two values, ignoring {@code NaN} unless both are {@code NaN}.
	 */
	public static float min(float a, float b) {
		if (Float.isNaN(a)) {
			return b;
		} else if (Float.isNaN(b)) {
			return a;
		} else {
			return Math.min(a, b);
		}
	}

	/**
	 * Checks if the value lies within {@code [start, end]}.
	 */
	public static boolean between(float val, float start, float end) {
		return compare(val, start) >= 0 && compare(val, end) <= 0;
	}

	/**
	 * Checks if the value lies within {@code [start, end)}.
	 */
	public static boolean rightIn(float val, float start, float end) {
		return compare(val, start) >= 0 && compare(val, end) < 0;
	}

	/**
	 * Checks if the value lies within {@code (start, end]}.
	 */
	public static boolean leftIn(float val, float start, float end) {
		return compare(val, start) > 0 && compare(val, end) <= 0;
	}

	/**
	 * Checks if the value lies within {@code (start, end)}.
	 */
	public static boolean in(float val, float start, float end) {
		return compare(val, start) > 0 && compare(val, end) < 0;
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(float[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(float[] src, Random rn) {
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
	public static int compare(float a, float b) {
		return Float.compare(a, b);
	}

	/**
	 * Returns a comparator that compares arrays lexicographically, element by element, then by length.
	 */
	public static Comparator<float[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	private static enum LexicographicalComparator implements Comparator<float[]> {
		INSTANCE;

		public int compare(float[] left, float[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Floats.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}
}
