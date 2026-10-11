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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharValueOverflowException;
import com.github.paganini2008.devtools.StringUtils;

/**
 * Null-safe utility methods for {@code char} values and arrays: searching, copying, sorting, set operations,
 * conversions and simple ASCII character classification.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Chars {

	public static final char CR = '\r';

	public static final char LF = '\n';

	private static final int MERGE_SORT_THRESHOLD = 10;

	public static final char[] EMPTY_ARRAY = new char[0];

	public static final Character[] EMPTY_OBJECT_ARRAY = new Character[0];

	/**
	 * Clones the given array, returning {@code null} for {@code null} input.
	 */
	public static char[] clone(char[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Returns the length of the given array, or {@code 0} for {@code null}.
	 */
	public static int length(char[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Creates a two-dimensional array whose elements are all set to the given default value (zero/false if absent).
	 */
	public static char[][] create(int yLength, int xLength, char defaultValue) {
		char[][] array = new char[yLength][];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	/**
	 * Creates an array of the given length whose elements are all set to the given default value (zero/false if absent).
	 */
	public static char[] create(int length, char defaultValue) {
		char[] array = new char[length];
		if (defaultValue != Character.MIN_VALUE) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	/**
	 * Checks if an array is neither {@code null} nor empty.
	 */
	public static boolean isNotEmpty(char[] args) {
		return !isEmpty(args);
	}

	/**
	 * Checks if an array is {@code null} or empty.
	 */
	public static boolean isEmpty(char[] args) {
		return args != null ? args.length == 0 : true;
	}

	/**
	 * Negation of {@link #contains}.
	 */
	public static boolean notContains(char[] a, int b) {
		return !contains(a, b);
	}

	/**
	 * Checks if the value is in the given array; a {@code null} array contains nothing.
	 */
	public static boolean contains(char[] a, int b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array, or {@code -1} if not found or the array is {@code null}.
	 */
	public static int indexOf(char[] a, int b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	/**
	 * Finds the index of the given value within {@code [start, end)} of the array.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int indexOf(char[] a, int b, int start, int end) {
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
	public static int lastIndexOf(char[] a, char b) {
		return lastIndexOf(a, b, a != null ? a.length - 1 : INDEX_NOT_FOUND);
	}

	/**
	 * Finds the last index of the given value, searching backwards from {@code start}.
	 * Returns {@code -1} if not found, if the array is {@code null} or if {@code start} is negative.
	 */
	public static int lastIndexOf(char[] a, char b, int start) {
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
	public static char[] concat(char[] left, char[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		char[] chars = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, chars, left.length, right.length);
		return chars;
	}

	/**
	 * Returns a new array with the given value appended.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static char[] add(char[] array, char a) {
		Assert.isNull(array, "Source array must not be null.");
		char[] chars = copy(array, array.length + 1);
		chars[chars.length - 1] = a;
		return chars;
	}

	/**
	 * Removes the first occurrence of the value, returning the original array if it is not found (or {@code null}).
	 */
	public static char[] remove(char[] array, char a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	/**
	 * Returns a new array without the element at {@code index}; a negative index counts from the end.
	 *
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static char[] removeAt(char[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			char[] target = create(length - 1, Character.MIN_VALUE);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	/**
	 * Returns a copy of the whole array.
	 */
	public static char[] copy(char[] array) {
		return copy(array, array.length);
	}

	/**
	 * Copies the first {@code length} elements into a new array, padding with zero/false if the array is shorter.
	 */
	public static char[] copy(char[] array, int length) {
		return copy(array, 0, length);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with zero/false.
	 */
	public static char[] copy(char[] array, int startIndex, int length) {
		return copy(array, startIndex, length, Character.MIN_VALUE);
	}

	/**
	 * Copies {@code length} elements starting at {@code startIndex} into a new array, padding with {@code defaultValue}.
	 */
	public static char[] copy(char[] array, int startIndex, int length, char defaultValue) {
		char[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(char[] src, int srcFrom, char[] dest, int destFrom, int length) {
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
	public static Character[] toWrappers(char[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		Character[] results = new Character[l];
		int i = 0;
		for (char arg : array) {
			results[i++] = Character.valueOf(arg);
		}
		return results;
	}

	/**
	 * Converts a wrapper array to a primitive array (see the overloads for how {@code null} elements are handled).
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static char[] toPrimitives(Character[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		char[] results = new char[l];
		int i = 0;
		for (Character arg : array) {
			if (arg != null) {
				results[i++] = arg.charValue();
			}
		}
		return ensureCapacity(results, i);
	}

	/**
	 * Truncates the array to {@code index} elements; returns the same array if its length already equals {@code index}.
	 */
	public static char[] ensureCapacity(char[] array, int index) {
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
	public static char[] expandCapacity(char[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	/**
	 * Returns a copy of the array enlarged by the given size (doubled if no size is given), padded with zero/false.
	 */
	public static char[] expandCapacity(char[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	/**
	 * Returns the elements joined by commas and enclosed in square brackets.
	 */
	public static String toString(char[] array) {
		return "[" + join(array) + "]";
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(char[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the elements with the delimiter ("," if absent, "" if {@code null}); returns "" for a {@code null} or empty array.
	 */
	public static String join(char[] array, String delimiter) {
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
	public static String join(char[] left, char[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	/**
	 * Joins pairs of elements at the same index with {@code conjunction}, separating pairs with {@code delimiter}.
	 * Only the common length is used; returns an empty string if either array is {@code null} or empty.
	 */
	public static String join(char[] left, char[] right, String conjunction, String delimiter) {
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
	public static void swap(char[] x, int a, int b) {
		char t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(char[] array, int low, int high, boolean asc) {
		for (int i = low; i < high; i++) {
			for (int j = i; j > low; j--) {
				if (asc ? array[j - 1] > array[j] : array[j - 1] < array[j]) {
					swap(array, j - 1, j);
				}
			}
		}
	}

	/**
	 * Sorts the array in ascending order in place; does nothing for {@code null} or empty arrays.
	 */
	public static void asc(char[] array) {
		if (isNotEmpty(array)) {
			char[] aux = (char[]) array.clone();
			mergeSort(aux, array, 0, array.length, true);
		}
	}

	/**
	 * Sorts the array in descending order in place; does nothing for {@code null} or empty arrays.
	 */
	public static void desc(char[] array) {
		if (isNotEmpty(array)) {
			char[] aux = (char[]) array.clone();
			mergeSort(aux, array, 0, array.length, false);
		}
	}

	private static void mergeSort(char[] src, char[] dest, int low, int high, boolean asc) {
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
	 */
	public static char[] unionAll(char[] left, char[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		char[] total = new char[left.length + right.length];
		int i = 0;
		for (char s : left) {
			total[i++] = s;
		}
		for (char s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the distinct elements of both arrays in encounter order. If one array is {@code null} the other one is returned as is.
	 */
	public static char[] union(char[] left, char[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		char[] total = new char[left.length + right.length];
		int i = 0;
		for (char s : left) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		for (char s : right) {
			if (indexOf(total, s, 0, i) == INDEX_NOT_FOUND) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

	/**
	 * Returns the elements of {@code left} that are not contained in {@code right}.
	 * If one array is {@code null} the other one is returned as is.
	 */
	public static char[] minus(char[] left, char[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		char[] result = new char[left.length];
		int i = 0;
		for (char s : left) {
			if (!contains(right, s)) {
				result[i++] = s;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns the elements of {@code left} that are also contained in {@code right}.
	 * If one array is {@code null} the other one is returned as is.
	 */
	public static char[] intersect(char[] left, char[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		char[] result = new char[left.length];
		int i = 0;
		for (char s : left) {
			if (contains(right, s)) {
				result[i++] = s;
			}
		}
		return ensureCapacity(result, i);
	}

	/**
	 * Returns the maximum of the array elements.
	 *
	 * @throws IllegalArgumentException if the array is {@code null} or empty
	 */
	public static char max(char[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		char max = array[0];
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
	public static char min(char[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		char min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min <= array[i] ? min : array[i];
		}
		return min;
	}

	/**
	 * Converts a collection to a primitive array, skipping {@code null} elements.
	 *
	 * @throws IllegalArgumentException if the collection is {@code null}
	 */
	public static char[] toArray(Collection<Character> c) {
		Assert.isNull(c, "Source collection must not be null.");
		char[] array = new char[c.size()];
		int i = 0;
		for (Character a : c) {
			if (a != null) {
				array[i++] = a.charValue();
			}
		}
		return ensureCapacity(array, i);
	}

	/**
	 * Converts the array to a new mutable list of wrappers.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static List<Character> toList(char[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Character> set = new ArrayList<Character>(array.length);
		for (char a : array) {
			set.add(Character.valueOf(a));
		}
		return set;
	}

	/**
	 * Reverses the array in place; does nothing for {@code null}.
	 */
	public static void reverse(char[] src) {
		if (src == null) {
			return;
		}
		int j;
		char t, a;
		for (int i = 0, l = src.length; i < l / 2; i++) {
			t = src[i];
			j = l - 1 - i;
			a = src[j];
			src[i] = a;
			src[j] = t;
		}
	}

	/**
	 * Converts a boolean to 'Y'/'N'.
	 */
	public static char cast(boolean b) {
		return b ? 'Y' : 'N';
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static char[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		char[] result = new char[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	/**
	 * Converts the value to {@code char}, throwing an {@code IllegalArgumentException} if it does not fit.
	 */
	public static char cast(int value) {
		if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
			throw CharValueOverflowException.causedByChar(value);
		}
		return (char) value;
	}

	/**
	 * Converts each element with the corresponding {@code cast} method.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static char[] casts(int[] value) {
		return casts(value, true);
	}

	/**
	 * Converts each element with the corresponding {@code cast} method. If {@code thrown} is false, conversion failures are ignored instead of rethrown.
	 *
	 * @throws IllegalArgumentException if the array is {@code null}
	 */
	public static char[] casts(int[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		char[] result = new char[value.length];
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
		return ensureCapacity(result, i);
	}

	/**
	 * Converts true/false to 'Y'/'N', returning the default value (null if absent) for {@code null}.
	 */
	public static Character valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true/false to 'Y'/'N', returning the default value (null if absent) for {@code null}.
	 */
	public static Character valueOf(Boolean b, Character defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Character.valueOf(cast(b));
	}

	/**
	 * Converts the code point to a character, returning the default value (null if absent) for {@code null} or out-of-range values.
	 */
	public static Character valueOf(Integer i) {
		return valueOf(i, null);
	}

	/**
	 * Converts the code point to a character, returning the default value (null if absent) for {@code null} or out-of-range values.
	 */
	public static Character valueOf(Integer i, Character defaultValue) {
		if (i == null) {
			return defaultValue;
		}
		try {
			return Character.valueOf(cast(i));
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts a character sequence to a list of characters.
	 */
	public static List<Character> toCharacterList(CharSequence value) {
		List<Character> list = new ArrayList<Character>();
		for (int i = 0; i < value.length(); i++) {
			list.add(Character.valueOf(value.charAt(i)));
		}
		return list;
	}

	/**
	 * Converts a character sequence to an array of characters.
	 */
	public static Character[] toCharacterArray(CharSequence value) {
		int length = value.length();
		Character[] result = new Character[length];
		for (int i = 0; i < length; i++) {
			result[i] = Character.valueOf(value.charAt(i));
		}
		return result;
	}

	/**
	 * Returns the hash code of a single value, consistent with the wrapper type.
	 */
	public static int hashCode(char arg) {
		return Character.hashCode(arg);
	}

	/**
	 * Compares two arrays element by element; two {@code null} arrays are equal.
	 */
	public static boolean deepEquals(char[] left, char[] right) {
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
	public static int deepHashCode(char[] args) {
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
	 * Returns the first character of the string, or the default value (null if absent) if it is blank.
	 */
	public static Character valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Returns the first character of the string, or the default value (null if absent) if it is blank.
	 */
	public static Character valueOf(String str, Character defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		return Character.valueOf(str.charAt(0));
	}

	/**
	 * Checks if the character is an ASCII letter.
	 */
	public static boolean isAlpha(char ch) {
		return isUpper(ch) || isLower(ch);
	}

	/**
	 * Checks if the character is a printable ASCII character (32-126).
	 */
	public static boolean isPrintable(char ch) {
		return ch >= 32 && ch < 127;
	}

	/**
	 * Checks if the character is an ASCII control character.
	 */
	public static boolean isControl(char ch) {
		return ch < 32 || ch == 127;
	}

	/**
	 * Checks if the character is an ASCII character.
	 */
	public static boolean isAscii(char ch) {
		return ch < 128;
	}

	/**
	 * Checks if the character is an ASCII upper case letter.
	 */
	public static boolean isUpper(char ch) {
		return ch >= 'A' && ch <= 'Z';
	}

	/**
	 * Checks if the character is an ASCII lower case letter.
	 */
	public static boolean isLower(char ch) {
		return ch >= 'a' && ch <= 'z';
	}

	/**
	 * Checks if the character is an ASCII digit.
	 */
	public static boolean isNumber(char ch) {
		return ch >= '0' && ch <= '9';
	}

	/**
	 * Checks if the character is an ASCII letter or digit.
	 */
	public static boolean isAlphaNumber(char ch) {
		return isAlpha(ch) || isNumber(ch);
	}

	/**
	 * Returns the Java unicode escape sequence of the character: a backslash, 'u' and four lower-case hex digits.
	 */
	public static String toUnicode(char ch) {
		if (ch < 0x10) {
			return "\\u000" + Integer.toHexString(ch);
		} else if (ch < 0x100) {
			return "\\u00" + Integer.toHexString(ch);
		} else if (ch < 0x1000) {
			return "\\u0" + Integer.toHexString(ch);
		}
		return "\\u" + Integer.toHexString(ch);
	}

	/**
	 * Converts each element to a string; returns {@code null} for a {@code null} array.
	 */
	public static String[] toStringArray(char[] args) {
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
	public static boolean isSameLength(char[] left, char[] right) {
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
	public static boolean isSubarray(char[] left, char[] right) {
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
	public static boolean containsAll(char[] left, char[] right) {
		if (isEmpty(left) || isEmpty(right)) {
			return false;
		}
		for (char c : right) {
			if (notContains(left, c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all elements are equal; returns {@code false} for a {@code null} or empty array.
	 */
	public static boolean same(char[] array) {
		if (isEmpty(array)) {
			return false;
		}
		for (int i = 1; i < array.length; i++) {
			if (array[i] - array[i - 1] != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates the array to the left by {@code n} positions in place.
	 */
	public static void leftScroll(char[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n % l);
		}
	}

	/**
	 * Rotates the array to the right by {@code n} positions in place.
	 */
	public static void rightScroll(char[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(char[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(char[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	/**
	 * Randomly permutes the array in place (Fisher-Yates); does nothing for {@code null}.
	 */
	public static void shuffle(char[] src, Random rn) {
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
	public static Comparator<char[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	/**
	 * Compares two values; the result is negative, zero or positive.
	 */
	public static int compare(char a, char b) {
		return a - b;
	}

	private static enum LexicographicalComparator implements Comparator<char[]> {

		INSTANCE;

		public int compare(char[] left, char[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Chars.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}
}
