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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * Utility methods for object arrays and lookups in primitive arrays, such as set
 * operations, searching, sorting, copying, joining and scrolling.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class ArrayUtils {

	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	public static final int INDEX_NOT_FOUND = -1;

	public static final int MERGE_SORT_THRESHOLD = 10;

	/**
	 * Negation of {@link #isSameLength(Object[], Object[])}.
	 */
	public static boolean isNotSameLength(Object[] left, Object[] right) {
		return !isSameLength(left, right);
	}

	/**
	 * Checks whether two arrays have the same length, treating null as an empty
	 * array.
	 *
	 * <pre>
	 * ArrayUtils.isSameLength(null, null)        = true
	 * ArrayUtils.isSameLength(null, [])          = true
	 * ArrayUtils.isSameLength([x], null)         = false
	 * ArrayUtils.isSameLength([x, y], [a, b])    = true
	 * </pre>
	 */
	public static boolean isSameLength(Object[] left, Object[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	/**
	 * Returns the length of the array, or 0 if it is null.
	 */
	public static int getLength(Object[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Reverses the array in place. A null array is ignored.
	 */
	public static void reverse(Object[] src) {
		if (src == null) {
			return;
		}
		int j;
		Object t, a;
		for (int i = 0, l = src.length; i < l / 2; i++) {
			t = src[i];
			j = l - 1 - i;
			a = src[j];
			src[i] = a;
			src[j] = t;
		}
	}

	/**
	 * Swaps two elements of the array in place.
	 */
	public static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	/**
	 * Concatenates two arrays, keeping duplicates. If one of them is null, the
	 * other one is returned.
	 *
	 * <pre>
	 * ArrayUtils.unionAll(null, null)        = null
	 * ArrayUtils.unionAll([a, b], [b, c])    = [a, b, b, c]
	 * </pre>
	 */
	public static <T> T[] unionAll(T[] a, T[] b) {
		if (a == null && b == null) {
			return null;
		}
		if (a != null && b == null) {
			return a;
		}
		if (a == null && b != null) {
			return b;
		}
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length);
		int i = 0;
		for (T t : a) {
			result[i++] = t;
		}
		for (T t : b) {
			result[i++] = t;
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one of
	 * them is null, the other one is returned as it is.
	 *
	 * <pre>
	 * ArrayUtils.union([a, b, a], [b, c]) = [a, b, c]
	 * </pre>
	 */
	public static <T> T[] union(T[] a, T[] b) {
		if (a == null && b == null) {
			return null;
		}
		if (a != null && b == null) {
			return a;
		}
		if (a == null && b != null) {
			return b;
		}
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length);
		int i = 0;
		for (T t : a) {
			if (indexOf(result, t, 0, i) == INDEX_NOT_FOUND) {
				result[i++] = t;
			}
		}
		for (T t : b) {
			if (indexOf(result, t, 0, i) == INDEX_NOT_FOUND) {
				result[i++] = t;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns the elements of {@code a} that are not contained in {@code b}.
	 * If {@code b} is null, {@code a} is returned; if {@code a} is null,
	 * {@code b} is returned (legacy behavior).
	 *
	 * <pre>
	 * ArrayUtils.minus([a, b, c], [b, x]) = [a, c]
	 * </pre>
	 */
	public static <T> T[] minus(T[] a, T[] b) {
		if (a == null && b == null) {
			return null;
		}
		if (a != null && b == null) {
			return a;
		}
		if (a == null && b != null) {
			return b;
		}
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length);
		int i = 0;
		for (T t : a) {
			if (!contains(b, t)) {
				result[i++] = t;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns the elements of {@code a} that are also contained in {@code b}.
	 * If one of them is null, the other one is returned (legacy behavior).
	 *
	 * <pre>
	 * ArrayUtils.intersect([a, b, c], [c, b, x]) = [b, c]
	 * </pre>
	 */
	public static <T> T[] intersect(T[] a, T[] b) {
		if (a == null && b == null) {
			return null;
		}
		if (a != null && b == null) {
			return a;
		}
		if (a == null && b != null) {
			return b;
		}
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length);
		int i = 0;
		for (T t : a) {
			if (contains(b, t)) {
				result[i++] = t;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Negation of {@link #contains(Object[], Object)}.
	 */
	public static boolean notContains(Object[] a, Object b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the array contains the object (compared with equals). A null array
	 * returns false.
	 */
	public static boolean contains(Object[] a, Object b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the object (compared with equals).
	 *
	 * <pre>
	 * ArrayUtils.indexOf(null, "a")           = -1
	 * ArrayUtils.indexOf(["a", null], null)   = 1
	 * ArrayUtils.indexOf(["a", "b"], "z")     = -1
	 * </pre>
	 */
	public static int indexOf(Object[] a, Object b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the object within [start, end). Returns -1 if the
	 * array is null, start is negative or nothing is found.
	 */
	public static int indexOf(Object[] a, Object b, int start, int end) {
		if (a == null) {
			return INDEX_NOT_FOUND;
		}
		if (start < 0) {
			return INDEX_NOT_FOUND;
		}
		for (int i = start, l = Math.min(a.length, end); i < l; i++) {
			if (ObjectUtils.equals(a[i], b)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the last index of the object. A null array returns -1.
	 */
	public static int lastIndexOf(Object[] a, Object b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : -1);
	}

	/**
	 * Finds the last index of the object, searching backwards from {@code start}.
	 * Returns -1 if the array is null, start is negative or nothing is found.
	 */
	public static int lastIndexOf(Object[] a, Object b, int start) {
		if (a == null || start < 0) {
			return INDEX_NOT_FOUND;
		}
		for (int i = Math.min(start, a.length - 1); i >= 0; i--) {
			if (ObjectUtils.equals(a[i], b)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Returns the array truncated to {@code index} elements, or the array itself
	 * if its length already equals {@code index}. The array is never enlarged.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T> T[] ensureCapacity(T[] array, int index) {
		Assert.isNull(array);
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	/**
	 * Returns a copy of the array with doubled length, padded with nulls.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T> T[] expandCapacity(T[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by {@code size} elements (never shrunk).
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T> T[] expandCapacity(T[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	/**
	 * Checks if the array is null or empty.
	 */
	public static boolean isEmpty(Object[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Checks if the array is neither null nor empty.
	 */
	public static boolean isNotEmpty(Object[] args) {
		return !isEmpty(args);
	}

	/**
	 * Sorts the array in ascending natural order (stable merge sort).
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T extends Comparable<T>> void asc(T[] array) {
		Assert.isNull(array, "Array must not be null.");
		if (array.length > 0) {
			T[] aux = (T[]) array.clone();
			mergeSort(aux, array, 0, array.length, new Comparator<T>() {
				public int compare(T left, T right) {
					return left.compareTo(right);
				}
			});
		}
	}

	/**
	 * Sorts the array in descending natural order (stable merge sort).
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T extends Comparable<T>> void desc(T[] array) {
		Assert.isNull(array, "Array must not be null.");
		if (array.length > 0) {
			T[] aux = (T[]) array.clone();
			mergeSort(aux, array, 0, array.length, new Comparator<T>() {
				public int compare(T left, T right) {
					return right.compareTo(left);
				}
			});
		}
	}

	/**
	 * Sorts the array with the comparator (stable merge sort).
	 *
	 * @throws IllegalArgumentException if the array or comparator is null
	 */
	public static <T> void sort(T[] array, Comparator<T> c) {
		Assert.isNull(array, "Array must not be null.");
		Assert.isNull(c, "Comparator must not be null.");
		if (array.length > 0) {
			T[] aux = (T[]) array.clone();
			mergeSort(aux, array, 0, array.length, c);
		}
	}

	private static <T> void mergeSort(T[] src, T[] dest, int low, int high, Comparator<T> c) {
		int length = high - low;
		if (length < MERGE_SORT_THRESHOLD * 2) {
			sort(dest, low, high, c);
			return;
		}
		int mid = (high + low) / 2;
		mergeSort(dest, src, low, mid, c);
		mergeSort(dest, src, mid, high, c);
		int i = low;
		int p = low;
		int q = mid;
		while (p < mid && q < high) {
			if (c.compare(src[p], src[q]) <= 0) {
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

	private static <T> void sort(T[] dest, int low, int high, Comparator<T> c) {
		for (int i = low; i < high; i++) {
			for (int j = i; j > low; j--) {
				if (c.compare(dest[j - 1], dest[j]) > 0) {
					swap(dest, j - 1, j);
				}
			}
		}
	}

	/**
	 * Creates a two-dimensional array of the component type.
	 */
	public static <T> T[][] newArray(Class<?> type, int row, int column) {
		return newArray(type, row, column, null);
	}

	/**
	 * Creates a two-dimensional array of the component type filled with the default
	 * value.
	 *
	 * @throws IllegalArgumentException if type is null
	 */
	public static <T> T[][] newArray(Class<?> type, int row, int column, T defaultValue) {
		Assert.isNull(type, "Array class must not be null.");
		T[][] array = (T[][]) Array.newInstance(type, row, column);
		for (int i = 0; i < row; i++) {
			array[i] = newArray(type, column, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the component type.
	 */
	public static <T> T[] newArray(Class<?> type, int length) {
		return newArray(type, length, null);
	}

	/**
	 * Creates an array of the component type filled with the default value.
	 *
	 * @throws IllegalArgumentException if type is null
	 */
	public static <T> T[] newArray(Class<?> type, int length, T defaultValue) {
		Assert.isNull(type, "Array class must not be null.");
		T[] array = (T[]) Array.newInstance(type, length);
		if (defaultValue != null) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	/**
	 * Concatenates two arrays into a new array.
	 *
	 * @throws IllegalArgumentException if either array is null
	 */
	public static <T> T[] concat(T[] left, T[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		T[] other = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, other, left.length, right.length);
		return other;
	}

	/**
	 * Returns a new array with the element appended.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T> T[] add(T[] array, T a) {
		Assert.isNull(array, "Source array must not be null.");
		T[] other = copy(array, 0, array.length + 1);
		other[other.length - 1] = a;
		return other;
	}

	/**
	 * Returns a new array without the first occurrence of the element, or the same
	 * array if the element is not found.
	 */
	public static <T> T[] remove(T[] array, T a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at the index. A negative index counts
	 * from the end.
	 *
	 * <pre>
	 * ArrayUtils.removeAt([a, b], 0)  = [b]
	 * ArrayUtils.removeAt([a, b], -1) = [a]
	 * </pre>
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is null
	 */
	public static <T> T[] removeAt(T[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			T[] target = newArray(array.getClass().getComponentType(), length - 1);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a shallow copy of the array.
	 */
	public static <T> T[] copy(T[] array) {
		return copy(array, 0);
	}

	/**
	 * Returns a copy of the elements from {@code startIndex} to the end.
	 */
	public static <T> T[] copy(T[] array, int startIndex) {
		return copy(array, startIndex, array.length - startIndex);
	}

	/**
	 * Returns a new array of {@code length} elements copied from
	 * {@code startIndex}, padded with nulls if the source has fewer elements.
	 */
	public static <T> T[] copy(T[] array, int startIndex, int length) {
		return copy(array, startIndex, length, null);
	}

	/**
	 * Returns a new array of {@code length} elements copied from
	 * {@code startIndex}, padded with {@code defaultValue} if the source has fewer
	 * elements.
	 *
	 * <pre>
	 * ArrayUtils.copy([a, b, c], 1)         = [b, c]
	 * ArrayUtils.copy([a, b, c], 2, 3, "z") = [c, z, z]
	 * </pre>
	 */
	public static <T> T[] copy(T[] array, int startIndex, int length, T defaultValue) {
		T[] target = newArray(array.getClass().getComponentType(), length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static <T> void hardCopy(T[] src, int srcFrom, T[] dest, int destFrom, int length) {
		int n = Math.min(Math.min(src.length - srcFrom, dest.length - destFrom), length);
		if (n > 0) {
			System.arraycopy(src, srcFrom, dest, destFrom, n);
		}
	}

	/**
	 * Returns the trimmed elements joined with a comma and wrapped in brackets,
	 * e.g. "[a,,1]". Null elements become empty strings.
	 */
	public static String toString(Object[] array) {
		return toString(array, ",");
	}

	/**
	 * Returns the trimmed elements joined with the delimiter and wrapped in brackets.
	 */
	public static String toString(Object[] array, String delimiter) {
		return "[" + join(array, delimiter) + "]";
	}

	/**
	 * Joins the trimmed elements with a comma. Null or empty arrays return "".
	 */
	public static <T> String join(T[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the trimmed elements with the delimiter.
	 */
	public static <T> String join(T[] array, String delimiter) {
		return join(array, delimiter, true);
	}

	/**
	 * Joins the elements with the delimiter, optionally trimming each of them.
	 */
	public static <T> String join(T[] array, String delimiter, boolean trim) {
		return join(array, t -> t, delimiter, trim);
	}

	/**
	 * Joins the values extracted by the getter. Null values become empty strings, a
	 * null delimiter is treated as "" and a null or empty array returns "".
	 *
	 * <pre>
	 * ArrayUtils.join([" a ", null, 1], s -&gt; s, ",", true) = "a,,1"
	 * </pre>
	 */
	public static <T> String join(T[] array, Function<T, Object> getter, String delimiter, boolean trim) {
		int l;
		if (array == null || (l = array.length) == 0) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < l; i++) {
			content.append(ObjectUtils.toString(getter.apply(array[i]), trim, ""));
			if (i != l - 1) {
				content.append(delimiter);
			}
		}
		return content.toString();
	}

	/**
	 * Joins two arrays pairwise with a comma used as both conjunction and delimiter.
	 */
	public static <X, Y> String join(X[] left, Y[] right) {
		return join(left, right, ",");
	}

	/**
	 * Joins two arrays pairwise, using the delimiter as both conjunction and
	 * delimiter.
	 */
	public static <X, Y> String join(X[] left, Y[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins two arrays pairwise up to the shorter length.
	 *
	 * <pre>
	 * ArrayUtils.join([a, b, c], [1, 2], "=", "&amp;") = "a=1&amp;b=2"
	 * </pre>
	 */
	public static <X, Y> String join(X[] left, Y[] right, String conjunction, String delimiter) {
		return join(left, right, conjunction, delimiter, true);
	}

	/**
	 * Joins two arrays pairwise, optionally trimming each element.
	 */
	public static <X, Y> String join(X[] left, Y[] right, String conjunction, String delimiter, boolean trim) {
		return join(left, x -> x, right, y -> y, conjunction, delimiter, trim);
	}

	/**
	 * Joins the values extracted from two arrays pairwise up to the shorter length.
	 * Returns "" if either array is null or empty.
	 */
	public static <X, Y> String join(X[] left, Function<X, Object> leftGetter, Y[] right, Function<Y, Object> rightGetter,
			String conjunction, String delimiter, boolean trim) {
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
			content.append(ObjectUtils.toString(leftGetter.apply(left[i]), trim, "")).append(conjunction)
					.append(ObjectUtils.toString(rightGetter.apply(right[i]), trim, ""));
			if (i != l - 1) {
				content.append(delimiter);
			}
		}
		return content.toString();
	}

	/**
	 * Checks whether two arrays have the same component type, length and deeply
	 * equal elements (nested arrays are compared by content).
	 */
	public static boolean deepEquals(Object[] left, Object[] right) {
		if (left == right) {
			return true;
		}
		if (left == null) {
			return right == null;
		} else if (right == null) {
			return false;
		}
		if (left.getClass().getComponentType() != right.getClass().getComponentType()) {
			return false;
		}
		int length = left.length;
		if (length != right.length) {
			return false;
		}
		Object o1, o2;
		for (int i = 0; i < length; i++) {
			o1 = left[i];
			o2 = right[i];
			if (!ObjectUtils.deepEquals(o1, o2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a hash code based on the deep contents of the array; 0 for a null or
	 * empty array.
	 */
	public static int deepHashCode(Object[] args) {
		if (isEmpty(args)) {
			return 0;
		}
		int prime = 31;
		int hash = 1;
		for (int i = 0; i < args.length; i++) {
			hash = prime * hash + ObjectUtils.deepHashCode(args[i]);
		}
		return hash;
	}

	/**
	 * Converts each element to a string (null to ""). A null array returns null.
	 */
	public static String[] toStringArray(Object[] args, boolean trim) {
		if (args == null) {
			return null;
		}
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = ObjectUtils.toString(args[i], trim, "");
		}
		return array;
	}

	/**
	 * Checks whether {@code right} occurs as a contiguous sequence in {@code left}.
	 * Returns false if either array is empty.
	 *
	 * <pre>
	 * ArrayUtils.isSubarray([a, b, c, d], [b, c]) = true
	 * ArrayUtils.isSubarray([a, b, c, d], [b, d]) = false
	 * </pre>
	 */
	public static boolean isSubarray(Object[] left, Object[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (int i = 0; i < left.length; i++) {
			if (ObjectUtils.equals(left[i], right[0])) {
				boolean f = true;
				for (int j = 1; j < right.length; j++) {
					if (i + j == left.length) {
						return false;
					}
					if (ObjectUtils.notEquals(left[i + j], right[j])) {
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
	 * Checks whether {@code left} contains all elements of {@code right}. Returns
	 * false if either array is empty or {@code right} is longer than {@code left}.
	 */
	public static boolean containsAll(Object[] left, Object[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (Object o : right) {
			if (notContains(left, o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether all elements are equal. A null or empty array returns false.
	 */
	public static boolean same(Object[] array) {
		if (isEmpty(array)) {
			return false;
		}
		for (int i = 1; i < array.length; i++) {
			if (ObjectUtils.notEquals(array[i], array[i - 1])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 *
	 * <pre>
	 * [1, 2, 3, 4, 5] leftScroll 2 = [3, 4, 5, 1, 2]
	 * </pre>
	 */
	public static void leftScroll(Object[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			if (n > 0) {
				rightScroll(src, l - n);
			}
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 *
	 * <pre>
	 * [1, 2, 3, 4, 5] rightScroll 2 = [4, 5, 1, 2, 3]
	 * </pre>
	 */
	public static void rightScroll(Object[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(Object[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Slices the array into chunks of {@code n} elements; the last chunk may be
	 * shorter.
	 *
	 * <pre>
	 * ArrayUtils.slice([1, 2, 3, 4, 5], 2) = [[1, 2], [3, 4], [5]]
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the array is empty or n &lt;= 0
	 */
	public static Object[][] slice(Object[] src, int n) {
		Assert.isTrue(isEmpty(src), "Empty array.");
		Assert.isTrue(n <= 0, "Slice size must be greater than 0.");
		int srcLen = src.length;
		int length = srcLen % n == 0 ? srcLen / n : srcLen / n + 1;
		Object[][] results = new Object[length][];
		int startIndex = 0;
		for (int i = 0; i < length - 1; i++) {
			results[i] = copy(src, startIndex, n);
			startIndex += n;
		}
		results[length - 1] = copy(src, startIndex, srcLen - startIndex);
		return results;
	}

	/**
	 * Divides the array into {@code n} parts of {@code length / n} elements, the
	 * last part taking the rest. Surplus parts are empty.
	 *
	 * <pre>
	 * ArrayUtils.divide([1, 2, 3, 4, 5], 2) = [[1, 2], [3, 4, 5]]
	 * ArrayUtils.divide([1, 2], 4)          = [[1], [2], [], []]
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the array is empty or n &lt;= 0
	 */
	public static Object[][] divide(Object[] src, int n) {
		Assert.isTrue(isEmpty(src), "Empty array.");
		Assert.isTrue(n <= 0, "Number of parts must be greater than 0.");
		Object[][] results = new Object[n][];
		int startIndex = 0;
		int length = src.length / n;
		if (length < 1) {
			length = 1;
		}
		int endIndex;
		for (int i = 0; i < n - 1; i++) {
			endIndex = Math.min(startIndex + length, src.length);
			results[i] = Arrays.copyOfRange(src, startIndex, endIndex);
			startIndex = endIndex;
		}
		results[n - 1] = Arrays.copyOfRange(src, startIndex, src.length);
		return results;
	}

	/**
	 * Randomly permutes the array in place. A null array is ignored.
	 */
	public static void shuffle(Object[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place with the given random source.
	 */
	public static void shuffle(Object[] src, Random rn) {
		if (src == null) {
			return;
		}
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	/**
	 * Checks if the char array does not contain the value.
	 */
	public static boolean notContains(char[] a, int b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the char array contains the value. A null array returns false.
	 */
	public static boolean contains(char[] a, int b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the char array, or -1.
	 */
	public static int indexOf(char[] a, int b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the char array within [start, end), or -1.
	 */
	public static int indexOf(char[] a, int b, int start, int end) {
		return Chars.indexOf(a, b, start, end);
	}

	/**
	 * Checks if the double array does not contain the value.
	 */
	public static boolean notContains(double[] a, double b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the double array contains the value. A null array returns false.
	 */
	public static boolean contains(double[] a, double b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the double array, or -1.
	 */
	public static int indexOf(double[] a, double b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the double array within [start, end), or -1.
	 */
	public static int indexOf(double[] a, double b, int start, int end) {
		return Doubles.indexOf(a, b, start, end);
	}

	/**
	 * Checks if the long array does not contain the value.
	 */
	public static boolean notContains(long[] a, long b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the long array contains the value. A null array returns false.
	 */
	public static boolean contains(long[] a, long b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the long array, or -1.
	 */
	public static int indexOf(long[] a, long b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the long array within [start, end), or -1.
	 */
	public static int indexOf(long[] a, long b, int start, int end) {
		return Longs.indexOf(a, b, start, end);
	}

	/**
	 * Checks if the float array does not contain the value.
	 */
	public static boolean notContains(float[] a, float b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the float array contains the value. A null array returns false.
	 */
	public static boolean contains(float[] a, float b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the float array, or -1.
	 */
	public static int indexOf(float[] a, float b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the float array within [start, end), or -1.
	 */
	public static int indexOf(float[] a, float b, int start, int end) {
		return Floats.indexOf(a, b, start, end);
	}

	/**
	 * Checks if the int array does not contain the value.
	 */
	public static boolean notContains(int[] a, int b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the int array contains the value. A null array returns false.
	 */
	public static boolean contains(int[] a, int b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the int array, or -1.
	 */
	public static int indexOf(int[] a, int b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the int array within [start, end), or -1.
	 */
	public static int indexOf(int[] a, int b, int start, int end) {
		return Ints.indexOf(a, b, start, end);
	}

	/**
	 * Checks if the short array does not contain the value.
	 */
	public static boolean notContains(short[] a, short b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the short array contains the value. A null array returns false.
	 */
	public static boolean contains(short[] a, short b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the short array, or -1.
	 */
	public static int indexOf(short[] a, short b) {
		return indexOf(a, b, 0);
	}

	/**
	 * Finds the first index of the value in the short array from {@code start}, or -1.
	 */
	public static int indexOf(short[] a, short b, int start) {
		return indexOf(a, b, start, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the short array within [start, end), or -1.
	 */
	public static int indexOf(short[] a, short b, int start, int end) {
		return Shorts.indexOf(a, b, start, end);
	}

	/**
	 * Checks if the boolean array does not contain the value.
	 */
	public static boolean notContains(boolean[] a, boolean b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the boolean array contains the value. A null array returns false.
	 */
	public static boolean contains(boolean[] a, boolean b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the first index of the value in the boolean array, or -1.
	 */
	public static int indexOf(boolean[] a, boolean b) {
		return indexOf(a, b, 0);
	}

	/**
	 * Finds the first index of the value in the boolean array from {@code start}, or -1.
	 */
	public static int indexOf(boolean[] a, boolean b, int start) {
		return indexOf(a, b, start, a != null ? a.length : 0);
	}

	/**
	 * Finds the first index of the value in the boolean array within [start, end), or -1.
	 */
	public static int indexOf(boolean[] a, boolean b, int start, int end) {
		return Booleans.indexOf(a, b, start, end);
	}

	/**
	 * Returns the first element, or null if the array is null or empty.
	 */
	public static Object getFirst(Object[] array) {
		return getFirst(array, null);
	}

	/**
	 * Returns the first element, or the default value if the array is null or empty.
	 */
	public static Object getFirst(Object[] array, Object defaultValue) {
		if (isEmpty(array)) {
			return defaultValue;
		}
		return array[0];
	}

	/**
	 * Returns the last element, or null if the array is null or empty.
	 */
	public static Object getLast(Object[] array) {
		return getLast(array, null);
	}

	/**
	 * Returns the last element, or the default value if the array is null or empty.
	 */
	public static Object getLast(Object[] array, Object defaultValue) {
		if (isEmpty(array)) {
			return defaultValue;
		}
		return array[array.length - 1];
	}

	/**
	 * Maps each element into a new array of the required type. A null array returns
	 * null.
	 */
	public static <E, T> T[] map(E[] array, Function<E, T> f, Class<T> requiredType) {
		if (array == null) {
			return null;
		}
		List<T> list = Arrays.stream(array).map(f).collect(Collectors.toList());
		return cast(list.toArray(), requiredType);
	}

	/**
	 * Copies the elements into a new array of the required type. A null array
	 * returns null.
	 *
	 * @throws ClassCastException if an element is not an instance of the type
	 */
	public static <T> T[] cast(Object[] array, Class<T> requiredType) {
		if (array == null) {
			return null;
		}
		T[] result = (T[]) Array.newInstance(requiredType, array.length);
		for (int i = 0; i < result.length; i++) {
			result[i] = requiredType.cast(array[i]);
		}
		return result;
	}
}
