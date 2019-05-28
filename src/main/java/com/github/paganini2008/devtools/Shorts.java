package com.github.paganini2008.devtools;

import static com.github.paganini2008.devtools.ArrayUtils.INDEX_NOT_FOUND;
import static com.github.paganini2008.devtools.ArrayUtils.MERGE_SORT_THRESHOLD;
import static com.github.paganini2008.devtools.ArrayUtils.rn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Short Utility
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Shorts {

	public static final short[] EMPTY_SHORT_ARRAY = new short[0];

	public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

	private static final LruMap<String, Short> cache = new LruMap<String, Short>(128);

	public static final int BYTES = Short.SIZE / Byte.SIZE;

	public static final short MAX_POWER_OF_TWO = 1 << (Short.SIZE - 2);

	public static void clearCache() {
		cache.clear();
	}

	private Shorts() {
	}

	public static short[] clone(short[] array) {
		return array != null ? array.clone() : null;
	}

	public static int length(short[] array) {
		return array != null ? array.length : 0;
	}

	public static short[][] create(int yLength, int xLength, short defaultValue) {
		short[][] array = new short[yLength][xLength];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	public static short[] create(int length, short defaultValue) {
		short[] array = new short[length];
		if (defaultValue != 0) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static boolean isNotEmpty(short[] args) {
		return !isEmpty(args);
	}

	public static boolean isEmpty(short[] args) {
		return args != null ? args.length == 0 : true;
	}

	public static boolean notContains(short[] a, short b) {
		return !contains(a, b);
	}

	public static boolean contains(short[] a, short b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(short[] a, short b) {
		return indexOf(a, b, 0);
	}

	public static int indexOf(short[] a, short b, int start) {
		return indexOf(a, b, start, a.length);
	}

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

	public static int lastIndexOf(short[] a, short b) {
		return lastIndexOf(a, b, a.length - 1);
	}

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

	public static short[] concat(short[] left, short[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		short[] shorts = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, shorts, left.length, right.length);
		return shorts;
	}

	public static short[] add(short[] array, short a) {
		Assert.isNull(array, "Source array must not be null.");
		short[] shorts = copy(array, array.length + 1);
		shorts[shorts.length - 1] = a;
		return shorts;
	}

	public static short[] remove(short[] array, short a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

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

	public static short[] copy(short[] array) {
		return copy(array, array.length);
	}

	public static short[] copy(short[] array, int length) {
		return copy(array, 0, length);
	}

	public static short[] copy(short[] array, int startIndex, int length) {
		return copy(array, startIndex, length, (short) 0);
	}

	public static short[] copy(short[] array, int startIndex, int length, short defaultValue) {
		short[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(short[] src, int srcFrom, short[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

	public static Short[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Short[] result = new Short[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Short.valueOf(value[i]);
		}
		return result;
	}

	public static Short[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Short[] result = new Short[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Short.valueOf(value[i]);
		}
		return result;
	}

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

	public static short[] ensureCapacity(short[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static short[] expandCapacity(short[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, index, array.length);
	}

	public static short[] expandCapacity(short[] array, int index, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	public static short max(short[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		short max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max >= array[i] ? max : array[i];
		}
		return max;
	}

	public static short min(short[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		short min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min <= array[i] ? min : array[i];
		}
		return min;
	}

	public static short sum(short[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		short sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	public static double avg(short[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	public static String toString(short[] array) {
		return "[" + join(array) + "]";
	}

	public static String join(short[] array) {
		return join(array, ",");
	}

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

	public static String join(short[] left, short[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

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

	public static void asc(short[] array) {
		if (isNotEmpty(array)) {
			short[] aux = (short[]) array.clone();
			mergeSort(aux, array, 0, array.length, true);
		}
	}

	public static void desc(short[] array) {
		if (isNotEmpty(array)) {
			short[] aux = (short[]) array.clone();
			mergeSort(aux, array, 0, array.length, false);
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
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		for (short s : right) {
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

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
		short[] result = new short[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (!contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

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
		short[] result = new short[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

	public static short[] toFloatArray(List<Short> list) {
		Assert.isNull(list, "Source list must not be null.");
		short[] array = new short[list.size()];
		int i = 0;
		for (Short a : list) {
			if (a != null) {
				array[i++] = a.shortValue();
			}
		}
		return ensureCapacity(array, i);
	}

	public static short[] toFloatArray(Set<Short> set) {
		Assert.isNull(set, "Source set must not be null.");
		short[] array = new short[set.size()];
		int i = 0;
		for (Short a : set) {
			if (a != null) {
				array[i++] = a.shortValue();
			}
		}
		return ensureCapacity(array, i);
	}

	public static List<Short> asList(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Short> set = new ArrayList<Short>(array.length);
		for (short a : array) {
			set.add(Short.valueOf(a));
		}
		return set;
	}

	public static Set<Short> asSet(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		Set<Short> set = new LinkedHashSet<Short>(array.length);
		for (short a : array) {
			set.add(Short.valueOf(a));
		}
		return set;
	}

	public static void reverse(short[] src) {
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

	public static int hashCode(short arg) {
		return arg;
	}

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

	public static Short valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static Short valueOf(Boolean b, Short defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Short.valueOf(cast(b));
	}

	public static Short valueOf(Character ch) {
		return valueOf(ch, null);
	}

	public static Short valueOf(Character c, Short defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return Short.valueOf(cast(c));
	}

	public static Short valueOf(String str) {
		return valueOf(str, null);
	}

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

	public static Short[] valuesOf(String[] strs) {
		return valuesOf(strs, null);
	}

	public static Short[] valuesOf(String[] strs, Short defaultValue) {
		Assert.isNull(strs, "Source array must not be null.");
		Short[] result = new Short[strs.length];
		int i = 0;
		for (String str : strs) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

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
		if (pooled == null) {
			String newStr = NumberUtils.read(str);
			if (!NumberUtils.isNumber(newStr)) {
				throw new NumberFormatException("Can not parse string for: " + str);
			}
			try {
				cache.put(str, Short.parseShort(newStr));
			} catch (NumberFormatException e) {
				cache.put(str, (short) Double.parseDouble(newStr));
			}
			pooled = cache.get(str);
		}
		return pooled.shortValue();
	}

	public static short[] parses(String[] strs) {
		return parses(strs, true);
	}

	public static short[] parses(String[] strs, boolean thrown) {
		Assert.isNull(strs, "Source array must not be null.");
		short[] result = new short[strs.length];
		int i = 0;
		short s;
		for (String str : strs) {
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

	public static short cast(boolean b) {
		return (short) (b ? 1 : 0);
	}

	public static short[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static short cast(char value) {
		short s = (short) value;
		if (s != value) {
			throw new NumberOverflowException();
		}
		return s;
	}

	public static short[] casts(char[] value) {
		return casts(value, true);
	}

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

	public static short[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		short[] result = new short[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static short[] casts(int[] value) {
		return casts(value, true);
	}

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

	public static short cast(Number value) {
		if (value instanceof BigDecimal) {
			return cast((BigDecimal) value);
		} else if (value instanceof BigInteger) {
			return cast((BigInteger) value);
		} else {
			return cast(value.longValue());
		}
	}

	public static short cast(long value) {
		short converted = (short) value;
		if (value != converted) {
			throw new NumberOverflowException();
		}
		return converted;
	}

	public static short cast(BigDecimal value) {
		return cast(value.toBigInteger());
	}

	public static short cast(BigInteger value) {
		if (NumberRange.checkShort(value) != 0) {
			throw new NumberOverflowException();
		}
		return value.shortValue();
	}

	public static short[] casts(long[] value) {
		return casts(value, true);
	}

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

	public static short cast(float value) {
		return cast((double) value);
	}

	public static short[] casts(float[] value) {
		return casts(value, true);
	}

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

	public static short[] casts(double[] value) {
		return casts(value, true);
	}

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

	public static short[] casts(Number[] array) {
		return casts(array, true);
	}

	public static short[] casts(Number[] array, boolean thrown) {
		Assert.isNull(array, "Source array must not be null.");
		short[] result = new short[array.length];
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

	public static Short valueOf(Number n) {
		return valueOf(n, null);
	}

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

	public static Short[] valuesOf(Number[] array) {
		return valuesOf(array, null);
	}

	public static Short[] valuesOf(Number[] array, Short defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Short[] result = new Short[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	public static int deepHashCode(short[] args) {
		Assert.isNull(args, "Source array must not be null.");
		int hash = 0;
		for (int i = 0; i < args.length; i++) {
			hash += hashCode(args[i]);
		}
		return hash;
	}

	public static String[] toStringArray(short[] args) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = String.valueOf(args[i]);
		}
		return array;
	}

	public static boolean isEven(short value) {
		return (value & 1) == 0;
	}

	public static boolean isEvens(short[] values) {
		for (short s : values) {
			if (isOdd(s)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isOdds(short[] values) {
		for (short s : values) {
			if (isEven(s)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isOdd(short value) {
		return !isEven(value);
	}

	public static boolean isSameLength(short[] left, short[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	public static boolean same(short[] array) {
		return isSequentially(array, 0);
	}

	public static boolean isSequentially(short[] array) {
		return isSequentially(array, 1);
	}

	public static boolean isSequentially(short[] array, int n) {
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

	public static boolean containsAll(short[] left, short[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (short s : right) {
			if (notContains(left, s)) {
				return false;
			}
		}
		return true;
	}

	public static void leftScroll(short[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n);
		}
	}

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

	public static void shuffle(short[] src) {
		shuffle(src, rn);
	}

	public static void shuffle(short[] src, Random rn) {
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	public static Comparator<short[]> lexicographicalComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	public static int compare(short a, short b) {
		return a - b;
	}

	private enum LexicographicalComparator implements Comparator<short[]> {

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
