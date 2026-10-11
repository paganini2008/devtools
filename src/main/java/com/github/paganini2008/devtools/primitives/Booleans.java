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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.BooleanFormatException;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * Null-safe utility methods for {@code boolean} values and arrays: searching, copying, joining, comparing
 * and lenient parsing of text such as "yes"/"no", "on"/"off" or "1"/"0".
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class Booleans {

	private static final List<String> trueOrFalses = new CopyOnWriteArrayList<String>(
			new String[] { "TRUE", "FALSE", "true", "false", "YES", "NO", "yes", "no", "ON", "OFF", "on", "off", "1", "0" });

	private static final List<Character> trueOrFalseChars = new CopyOnWriteArrayList<Character>(
			new Character[] { 'T', 'F', 't', 'f', 'Y', 'N', 'y', 'n', '1', '0' });

	public static final boolean[] EMPTY_ARRAY = new boolean[0];

	public static final Boolean[] EMPTY_OBJECT_ARRAY = new Boolean[0];

	/**
	 * Registers an additional pair of true/false literals recognized by {@code parse}. Thread-safe.
	 */
	public static void addTrueAndFalse(char trueChar, char falseChar) {
		// added as a pair atomically, so that an even index always means 'true'
		trueOrFalseChars.addAll(ListUtils.create(trueChar, falseChar));
	}

	/**
	 * Registers an additional pair of true/false literals recognized by {@code parse}. Thread-safe.
	 */
	public static void addTrueAndFalse(String trueString, String falseString) {
		// added as a pair atomically, so that an even index always means 'true'
		trueOrFalses.addAll(ListUtils.create(trueString, falseString));
	}

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static boolean[] clone(boolean[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(boolean[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static boolean[][] create(int yLength, int xLength) {
		return create(yLength, xLength, false);
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static boolean[][] create(int yLength, int xLength, boolean defaultValue) {
		boolean[][] array = new boolean[yLength][xLength];
		if (defaultValue) {
			for (int i = 0; i < yLength; i++) {
				array[i] = create(xLength, defaultValue);
			}
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static boolean[] create(int length) {
		return create(length, false);
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static boolean[] create(int length, boolean defaultValue) {
		boolean[] array = new boolean[length];
		if (defaultValue) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	/**
	 * Checks if an array is neither {@code null} nor empty.
	 */
	public static boolean isNotEmpty(boolean[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 */
	public static boolean isEmpty(boolean[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(boolean[] a, boolean b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(boolean[] a, boolean b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 */
	public static int indexOf(boolean[] a, boolean b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(boolean[] a, boolean b, int start, int end) {
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
	 */
	public static int lastIndexOf(boolean[] a, boolean b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(boolean[] a, boolean b, int start) {
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
	public static boolean[] concat(boolean[] left, boolean[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		boolean[] booleans = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, booleans, left.length, right.length);
		return booleans;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static boolean[] add(boolean[] array, boolean a) {
		Assert.isNull(array, "Source array must not be null.");
		boolean[] booleans = copy(array, array.length + 1);
		booleans[booleans.length - 1] = a;
		return booleans;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static boolean[] remove(boolean[] array, boolean a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static boolean[] removeAt(boolean[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			boolean[] target = create(length - 1, false);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static boolean[] copy(boolean[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static boolean[] copy(boolean[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static boolean[] copy(boolean[] array, int startIndex, int length) {
		return copy(array, startIndex, length, false);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static boolean[] copy(boolean[] array, int startIndex, int length, boolean defaultValue) {
		boolean[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(boolean[] src, int srcFrom, boolean[] dest, int destFrom, int length) {
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
	public static Boolean[] toWrappers(boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		Boolean[] results = new Boolean[l];
		int i = 0;
		for (boolean arg : array) {
			results[i++] = Boolean.valueOf(arg);
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static boolean[] toPrimitives(Boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		boolean[] results = new boolean[l];
		int i = 0;
		for (Boolean arg : array) {
			if (arg != null) {
				results[i++] = arg.booleanValue();
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static boolean[] ensureCapacity(boolean[] array, int index) {
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
	public static boolean[] expandCapacity(boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static boolean[] expandCapacity(boolean[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	/**
	 * Returns the elements joined by {@code delim} and enclosed in square brackets.
	 */
	public static String toString(boolean[] array, String delim) {
		return "[" + join(array, delim) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(boolean[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(boolean[] array, String delimiter) {
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
	public static String join(boolean[] left, boolean[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 */
	public static String join(boolean[] left, boolean[] right, String conjunction, String delimiter) {
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
	 * Converts a collection to a primitive array, skipping {@code null} elements.
	 *
	 * @throws IllegalArgumentException if the collection is {@code null}
	 */
	public static boolean[] toArray(Collection<Boolean> c) {
		Assert.isNull(c, "Source collection must not be null.");
		boolean[] array = new boolean[c.size()];
		int i = 0;
		for (Boolean a : c) {
			if (a != null) {
				array[i++] = a.booleanValue();
			}
		}
		return ensureCapacity(array, i);
	}

	/**
	 * Converts the array to a new mutable list of wrappers.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static List<Boolean> toList(boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Boolean> set = new ArrayList<Boolean>(array.length);
		for (boolean a : array) {
			set.add(Boolean.valueOf(a));
		}
		return set;
	}

	/**
	 * Returns the hash code of a single value, consistent with the wrapper type.
	 */
	public static int hashCode(boolean arg) {
		return Boolean.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(boolean[] left, boolean[] right) {
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
	 * Returns a content-based hash code of the array, or {@code 0} for a {@code null} array.
	 */
	public static int deepHashCode(boolean[] args) {
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

	private static int indexFor(String arg) {
		return trueOrFalses.indexOf(arg);
	}

	private static int indexFor(char arg) {
		return trueOrFalseChars.indexOf(arg);
	}

	/**
	 * Parses the character with {@link #parse(char)}, returning the default value (null if absent) if it is {@code null} or not recognized.
	 */
	public static Boolean valueOf(Character arg) {
		return valueOf(arg, null);
	}

	/**
	 * Parses the character with {@link #parse(char)}, returning the default value (null if absent) if it is {@code null} or not recognized.
	 */
	public static Boolean valueOf(Character arg, Boolean defaultValue) {
		if (arg == null) {
			return defaultValue;
		}
		try {
			return Boolean.valueOf(parse(arg));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string with {@link #parse(String)}, returning the default value (null if absent) if it is blank or not recognized.
	 * <pre>
	 * Booleans.valueOf("yes")   = true
	 * Booleans.valueOf("off")   = false
	 * Booleans.valueOf("maybe") = null
	 * </pre>
	 */
	public static Boolean valueOf(String arg) {
		return valueOf(arg, null);
	}

	/**
	 * Parses the string with {@link #parse(String)}, returning the default value (null if absent) if it is blank or not recognized.
	 * <pre>
	 * Booleans.valueOf("yes")   = true
	 * Booleans.valueOf("off")   = false
	 * Booleans.valueOf("maybe") = null
	 * </pre>
	 */
	public static Boolean valueOf(String arg, Boolean defaultValue) {
		if (StringUtils.isBlank(arg)) {
			return defaultValue;
		}
		try {
			return Boolean.valueOf(parse(arg));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Boolean[] valuesOf(String[] args) {
		return valuesOf(args, null);
	}

	/**
	 * Converts each element with the corresponding single-value {@code valueOf} method; failures yield {@code defaultValue}.
	 */
	public static Boolean[] valuesOf(String[] args, Boolean defaultValue) {
		Boolean[] result = new Boolean[args.length];
		int i = 0;
		for (String arg : args) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	/**
	 * Parses a character such as 'T'/'F', 'Y'/'N' or '1'/'0' (plus any registered via {@link #addTrueAndFalse(char, char)}).
	 *
	 * @throws BooleanFormatException if the character is not recognized
	 */
	public static boolean parse(char arg) {
		int i;
		if ((i = indexFor(arg)) != -1) {
			return Ints.isEven(i);
		}
		throw new BooleanFormatException(arg);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are left as false instead of causing an exception.
	 */
	public static boolean[] parseMany(char[] args) {
		return parseMany(args, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are left as false instead of causing an exception.
	 */
	public static boolean[] parseMany(char[] args, boolean thrown) {
		boolean[] result = new boolean[args.length];
		int i = 0;
		for (char arg : args) {
			try {
				result[i++] = parse(arg);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Parses strings such as "true"/"false", "yes"/"no", "on"/"off" or "1"/"0" (upper or lower case)
	 * plus any registered via {@link #addTrueAndFalse(String, String)}.
	 *
	 * @throws IllegalArgumentException if the string is blank, or a {@code BooleanFormatException} if it is not recognized
	 */
	public static boolean parse(String arg) {
		Assert.hasNoText(arg);
		int i;
		if ((i = indexFor(arg)) != -1) {
			return Ints.isEven(i);
		}
		throw new BooleanFormatException(arg);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are left as false instead of causing an exception.
	 */
	public static boolean[] parseMany(String[] args) {
		return parseMany(args, true);
	}

	/**
	 * Parses each element with {@code parse}. If {@code thrown} is false, elements that cannot be parsed
	 * are left as false instead of causing an exception.
	 */
	public static boolean[] parseMany(String[] args, boolean thrown) {
		boolean[] result = new boolean[args.length];
		int i = 0;
		for (String arg : args) {
			try {
				result[i++] = parse(arg);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns {@code true} if the number's long value is positive, or the default value (null if absent) for {@code null}.
	 */
	public static Boolean valueOf(Number n) {
		return valueOf(n, null);
	}

	/**
	 * Returns {@code true} if the number's long value is positive, or the default value (null if absent) for {@code null}.
	 */
	public static Boolean valueOf(Number n, Boolean defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		try {
			return n.longValue() > 0;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns "yes"/"no", or the default value ("no" if absent) for {@code null}.
	 */
	public static String toStringYesNo(Boolean result) {
		return toStringYesNo(result, "no");
	}

	/**
	 * Returns "yes"/"no", or the default value ("no" if absent) for {@code null}.
	 */
	public static String toStringYesNo(Boolean result, String defaultValue) {
		return toString(result, "yes", "no", defaultValue);
	}

	/**
	 * Returns "on"/"off", or the default value ("off" if absent) for {@code null}.
	 */
	public static String toStringOnOff(Boolean result) {
		return toStringOnOff(result, "off");
	}

	/**
	 * Returns "on"/"off", or the default value ("off" if absent) for {@code null}.
	 */
	public static String toStringOnOff(Boolean result, String defaultValue) {
		return toString(result, "on", "off", defaultValue);
	}

	/**
	 * Returns "true"/"false", or the default value ("false" if absent) for {@code null}.
	 */
	public static String toString(Boolean result) {
		return toString(result, "false");
	}

	/**
	 * Returns "true"/"false", or the default value ("false" if absent) for {@code null}.
	 */
	public static String toString(Boolean result, String defaultValue) {
		return toString(result, "true", "false", defaultValue);
	}

	/**
	 * Returns {@code yes} for true, {@code no} for false and {@code defaultValue} for {@code null}.
	 */
	public static String toString(Boolean result, String yes, String no, String defaultValue) {
		return result != null ? result.booleanValue() ? yes : no : defaultValue;
	}

	/**
	 * Converts each element to a string; returns {@code null} for a {@code null} array.
	 */
	public static String[] toStringArray(boolean[] args) {
		if (args == null) {
			return null;
		}
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = String.valueOf(args[i]);
		}
		return array;
	}

	/**
	 * Checks if two arrays have the same length, treating {@code null} as an empty array.
	 */
	public static boolean isSameLength(boolean[] left, boolean[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	/**
	 * Checks if {@code right} occurs as a contiguous run inside {@code left}; returns {@code false} if either is {@code null} or empty.
	 */
	public static boolean isSubarray(boolean[] left, boolean[] right) {
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
	 */
	public static boolean containsAll(boolean[] left, boolean[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (boolean b : right) {
			if (notContains(left, b)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all elements are equal; returns {@code false} for a {@code null} or empty array.
	 */
	public static boolean same(boolean[] array) {
		if (isEmpty(array)) {
			return false;
		}
		for (int i = 1; i < array.length; i++) {
			if (array[i] != array[i - 1]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a comparator that compares arrays lexicographically, element by element, then by length.
	 */
	public static Comparator<boolean[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	/**
	 * Compares two values; the result is negative, zero or positive.
	 */
	public static int compare(boolean a, boolean b) {
		return (a == b) ? 0 : (a ? 1 : -1);
	}

	private static enum LexicographicalComparator implements Comparator<boolean[]> {

		INSTANCE;

		public int compare(boolean[] left, boolean[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Booleans.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}

}
