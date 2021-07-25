/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Arrays;
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
 * 
 * Ints
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Ints {

	public static final int[] EMPTY_INT_ARRAY = new int[0];

	public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];

	public static final int BYTES = Integer.SIZE / Byte.SIZE;

	public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

	private static final LruMap<String, Integer> cache = new LruMap<String, Integer>(1024);

	public static void clearCache() {
		cache.clear();
	}

	public static int[] clone(int[] array) {
		return array != null ? array.clone() : null;
	}

	public static int length(int[] array) {
		return array != null ? array.length : 0;
	}

	public static int[][] create(int yLength, int xLength, int defaultValue) {
		int[][] array = new int[yLength][xLength];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	public static int[] create(int length, int defaultValue) {
		int[] array = new int[length];
		if (defaultValue != 0) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static boolean isNotEmpty(int[] args) {
		return !isEmpty(args);
	}

	public static boolean isEmpty(int[] args) {
		return args != null ? args.length == 0 : true;
	}

	public static boolean notContains(int[] a, int b) {
		return !contains(a, b);
	}

	public static boolean contains(int[] a, int b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(int[] a, int b) {
		return indexOf(a, b, 0, a != null ? a.length : 0);
	}

	public static int indexOf(int[] a, int b, int start, int end) {
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

	public static int lastIndexOf(int[] a, int b) {
		return lastIndexOf(a, b, a.length - 1);
	}

	public static int lastIndexOf(int[] a, int b, int start) {
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

	public static int[] concat(int[] left, int[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		int[] ints = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, ints, left.length, right.length);
		return ints;
	}

	public static int[] add(int[] array, int a) {
		Assert.isNull(array, "Source array must not be null.");
		int[] ints = copy(array, array.length + 1);
		ints[ints.length - 1] = a;
		return ints;
	}

	public static int[] remove(int[] array, int a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	public static int[] removeAt(int[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			int[] target = create(length - 1, 0);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	public static int[] copy(int[] array) {
		return copy(array, 0);
	}

	public static int[] copy(int[] array, int startIndex) {
		return copy(array, startIndex, array.length);
	}

	public static int[] copy(int[] array, int startIndex, int length) {
		return copy(array, startIndex, length, 0);
	}

	public static int[] copy(int[] array, int startIndex, int length, int defaultValue) {
		int[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(int[] src, int srcFrom, int[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

	public static Integer[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Integer[] result = new Integer[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Integer.valueOf(value[i]);
		}
		return result;
	}

	public static Integer[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Integer[] result = new Integer[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Integer.valueOf(value[i]);
		}
		return result;
	}

	public static Integer[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Integer[] result = new Integer[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Integer.valueOf(value[i]);
		}
		return result;
	}

	public static Integer[] toWrappers(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		Integer[] results = new Integer[l];
		int i = 0;
		for (int arg : array) {
			results[i++] = Integer.valueOf(arg);
		}
		return results;
	}

	public static int[] toPrimitives(Integer[] array) {
		Assert.isNull(array, "Source array must not be null.");
		int l = array.length;
		int[] results = new int[l];
		int i = 0;
		for (Integer arg : array) {
			if (arg != null) {
				results[i++] = arg.intValue();
			}
		}
		return ensureCapacity(results, i);
	}

	public static int[] ensureCapacity(int[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static int[] expandCapacity(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	public static int[] expandCapacity(int[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return Arrays.copyOf(array, Math.max(length + size, length));
	}

	public static int max(int[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		int max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max >= array[i] ? max : array[i];
		}
		return max;
	}

	public static int min(int[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		int min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min <= array[i] ? min : array[i];
		}
		return min;
	}

	public static int sum(int[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		int sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	public static double avg(int[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	public static String toString(int[] array) {
		return "[" + join(array) + "]";
	}

	public static String join(int[] array) {
		return join(array, ",");
	}

	public static String join(int[] array, String delimiter) {
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

	public static String join(int[] left, int[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	public static String join(int[] left, int[] right, String conjunction, String delimiter) {
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

	public static void swap(int[] x, int a, int b) {
		int t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void sort(int[] array, int low, int high, boolean asc) {
		for (int i = low; i < high; i++) {
			for (int j = i; j > low; j--) {
				if (asc ? array[j - 1] > array[j] : array[j - 1] < array[j]) {
					swap(array, j - 1, j);
				}
			}
		}
	}

	public static void sort(int[] array, boolean asc) {
		if (isNotEmpty(array)) {
			int[] aux = (int[]) array.clone();
			mergeSort(aux, array, 0, array.length, asc);
		}
	}

	private static void mergeSort(int[] src, int[] dest, int low, int high, boolean asc) {
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

	public static int[] unionAll(int[] left, int[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		int[] total = new int[left.length + right.length];
		int i = 0;
		for (int s : left) {
			total[i++] = s;
		}
		for (int s : right) {
			total[i++] = s;
		}
		return ensureCapacity(total, i);
	}

	public static int[] union(int[] left, int[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		int[] total = new int[left.length + right.length];
		int i = 0;
		for (int s : left) {
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		for (int s : right) {
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

	public static int[] minus(int[] left, int[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		int[] result = new int[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (!contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

	public static int[] intersect(int[] left, int[] right) {
		if (left == null && right == null) {
			return null;
		}
		if (left != null && right == null) {
			return left;
		}
		if (left == null && right != null) {
			return right;
		}
		int[] result = new int[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

	public static int[] toArray(Collection<?> collection, int defaultValue) {
		Assert.isNull(collection, "Source collection must not be null.");
		int[] array = new int[collection.size()];
		int i = 0;
		for (Object a : collection) {
			try {
				array[i++] = ((Number) a).intValue();
			} catch (RuntimeException e) {
			}
		}
		return ensureCapacity(array, i);
	}

	public static List<Integer> toList(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Integer> set = new ArrayList<Integer>(array.length);
		for (int a : array) {
			set.add(Integer.valueOf(a));
		}
		return set;
	}

	public static void reverse(int[] src) {
		int j;
		int t, a;
		for (int i = 0, l = src.length; i < l / 2; i++) {
			t = src[i];
			j = l - 1 - i;
			a = src[j];
			src[i] = a;
			src[j] = t;
		}
	}

	public static int hashCode(int arg) {
		return Integer.hashCode(arg);
	}

	public static boolean deepEquals(int[] left, int[] right) {
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

	public static Integer valueOf(Character c) {
		return valueOf(c, null);
	}

	public static Integer valueOf(Character c, Integer defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return Integer.valueOf(cast(c));
	}

	public static Integer valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static Integer valueOf(Boolean b, Integer defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Integer.valueOf(cast(b));
	}

	public static Integer valueOf(String str) {
		return valueOf(str, null);
	}

	public static Integer valueOf(String str, Integer defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(parse(str));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	public static Integer[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	public static Integer[] valueOf(String[] strings, Integer defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Integer[] result = new Integer[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	public static int[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	public static int[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		int[] result = new int[strings.length];
		int i = 0;
		int s;
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

	public static int parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
		}
		if (NumberUtils.isHex(str)) {
			try {
				return Integer.decode(str);
			} catch (NumberFormatException e) {
			}
		}
		return parseStrictly(str);
	}

	private static int parseStrictly(String str) {
		Integer pooled = cache.get(str);
		if (pooled == null) {
			String newStr = NumberUtils.read(str);
			if (!NumberUtils.isNumber(newStr)) {
				throw new NumberFormatException("Can not parse string for: " + str);
			}
			try {
				cache.put(str, Integer.parseInt(newStr));
			} catch (NumberFormatException e) {
				cache.put(str, (int) Double.parseDouble(newStr));
			}
			pooled = cache.get(str);
		}
		return pooled.intValue();
	}

	public static int[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static int[] casts(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static int[] casts(long[] value) {
		return casts(value, true);
	}

	public static int[] casts(long[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
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

	public static int[] casts(float[] value) {
		return casts(value, true);
	}

	public static int[] casts(float[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
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

	public static int cast(boolean b) {
		return (b ? 1 : 0);
	}

	public static int[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static int cast(char value) {
		return (int) value;
	}

	public static int[] casts(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
		int i = 0;
		for (char val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static int cast(long value) {
		int converted = (int) value;
		if (value != converted) {
			throw new NumberOverflowException(value);
		}
		return converted;
	}

	public static int cast(BigDecimal value) {
		return cast(value.toBigInteger());
	}

	public static int cast(BigInteger value) {
		if (NumberRangeAssert.checkInteger(value)) {
			throw new NumberOverflowException(value);
		}
		return value.intValue();
	}

	public static int[] casts(double[] value) {
		return casts(value, true);
	}

	public static int[] casts(double[] value, boolean thrown) {
		Assert.isNull(value, "Source array must not be null.");
		int[] result = new int[value.length];
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

	public static int cast(Number value) {
		if (value instanceof BigDecimal) {
			return cast((BigDecimal) value);
		} else if (value instanceof BigInteger) {
			return cast((BigInteger) value);
		} else {
			return cast(value.longValue());
		}
	}

	public static int[] casts(Boolean[] array) {
		return casts(array, true);
	}

	public static int[] casts(Boolean[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		int[] result = new int[array.length];
		int i = 0;
		for (Boolean b : array) {
			if (b != null) {
				result[i++] = cast(b);
			} else if (thrown) {
				throw new NullPointerException("Null value existed.");
			}
		}
		return ensureCapacity(result, i);
	}

	public static int[] casts(Character[] array) {
		return casts(array, true);
	}

	public static int[] casts(Character[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		int[] result = new int[array.length];
		int i = 0;
		for (Character c : array) {
			if (c != null) {
				result[i++] = cast(c);
			} else if (thrown) {
				throw new NullPointerException("Null value existed.");
			}
		}
		return ensureCapacity(result, i);
	}

	public static int[] casts(Number[] array) {
		return casts(array, true);
	}

	public static int[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		int[] result = new int[array.length];
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

	public static Integer valueOf(Number n) {
		return valueOf(n, null);
	}

	public static Integer valueOf(Number value, Integer defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Integer) {
			return (Integer) value;
		}
		try {
			return Integer.valueOf(cast(value));
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	public static Integer[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	public static Integer[] valueOf(Number[] array, Integer defaultValue) {
		Integer[] result = new Integer[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	public static int deepHashCode(int[] args) {
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

	public static boolean isEven(int value) {
		return (value & 1) == 0;
	}

	public static boolean isOdd(int value) {
		return !isEven(value);
	}

	public static boolean isEvens(int[] values) {
		for (int i : values) {
			if (isOdd(i)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isOdds(int[] values) {
		for (int i : values) {
			if (isEven(i)) {
				return false;
			}
		}
		return true;
	}

	public static String[] toStringArray(int[] args, DecimalFormat df) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = df != null ? df.format(args[i]) : String.valueOf(args[i]);
		}
		return array;
	}

	public static boolean isSameLength(int[] left, int[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	public static boolean same(int[] array) {
		return isSerial(array, 0);
	}

	public static boolean isSerial(int[] array) {
		return isSerial(array, 1);
	}

	public static boolean isSerial(int[] array, int n) {
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

	public static boolean isSubarray(int[] left, int[] right) {
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

	public static boolean containsAll(int[] left, int[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (int i : right) {
			if (notContains(left, i)) {
				return false;
			}
		}
		return true;
	}

	public static void leftScroll(int[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n);
		}
	}

	public static void rightScroll(int[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			n %= l;
			rightScroll(src, 0, l - n - 1);
			rightScroll(src, l - n, l - 1);
			rightScroll(src, 0, l - 1);
		}
	}

	private static void rightScroll(int[] src, int n, int m) {
		for (; n < m; n++, m--) {
			swap(src, m, n);
		}
	}

	public static void shuffle(int[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	public static void shuffle(int[] src, Random rn) {
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	public static Comparator<int[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	public static int compare(int a, int b) {
		return a - b;
	}

	private static enum LexicographicalComparator implements Comparator<int[]> {

		INSTANCE;

		public int compare(int[] left, int[] right) {
			int minLength = Math.min(left.length, right.length);
			for (int i = 0; i < minLength; i++) {
				int result = Ints.compare(left[i], right[i]);
				if (result != 0) {
					return result;
				}
			}
			return left.length - right.length;
		}
	}

	public static String toBinaryString(int number) {
		char[] chs = new char[Integer.SIZE];
		for (int i = 0, l = Integer.SIZE; i < l; i++) {
			chs[l - 1 - i] = (char) ((number >> i & 1) + '0');
		}
		return new String(chs);
	}

	public static int parseBinaryString(String binaryString) {
		if (binaryString.charAt(0) == '0') {
			return Integer.parseInt(binaryString, 2);
		}
		StringBuilder str = new StringBuilder();
		for (char bit : binaryString.toCharArray()) {
			str.append((bit == '0') ? 1 : 0);
		}
		int convertedInt = Integer.parseInt(str.toString(), 2);
		return -(convertedInt + 1);
	}

	public static int shiftLeft(int number, int amount) {
		String original = toBinaryString(number);
		StringBuilder dest = new StringBuilder();
		amount &= 31;
		dest.append(original.toCharArray(), amount, original.length() - amount);
		for (int i = 0; i < amount; i++) {
			dest.append(0);
		}
		return parseBinaryString(dest.toString());
	}

	public static int shiftRight(int number, int amount) {
		String original = toBinaryString(number);
		StringBuilder dest = new StringBuilder();
		amount &= 31;
		dest.append(original.toCharArray(), 0, original.length() - amount);
		for (int i = 0; i < amount; i++) {
			dest.insert(0, number < 0 ? 1 : 0);
		}
		return parseBinaryString(dest.toString());
	}

	public static int unsignedShiftRight(int number, int amount) {
		String original = toBinaryString(number);
		StringBuilder dest = new StringBuilder();
		amount &= 31;
		dest.append(original.toCharArray(), 0, original.length() - amount);
		for (int i = 0; i < amount; i++) {
			dest.insert(0, 0);
		}
		return Integer.parseInt(dest.toString(), 2);
	}

	public static int and(int left, int right) {
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

	public static int or(int left, int right) {
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

	public static int xor(int left, int right) {
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

	public static int not(int number) {
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
