package com.github.paganini2008.devtools.primitives;

import static com.github.paganini2008.devtools.ArrayUtils.INDEX_NOT_FOUND;
import static com.github.paganini2008.devtools.ArrayUtils.MERGE_SORT_THRESHOLD;
import static java.lang.Float.NEGATIVE_INFINITY;
import static java.lang.Float.POSITIVE_INFINITY;

import java.math.BigDecimal;
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
 * 
 * Floats
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public abstract class Floats {

	public static final float[] EMPTY_ARRAY = new float[0];

	public static final Float[] EMPTY_OBJECT_ARRAY = new Float[0];

	private static final LruMap<String, Float> cache = new LruMap<String, Float>(128);

	public static final int BYTES = Float.SIZE / Byte.SIZE;

	public static void clearCache() {
		cache.clear();
	}

	public static float[] clone(float[] array) {
		return array != null ? array.clone() : null;
	}

	public static int length(float[] array) {
		return array != null ? array.length : 0;
	}

	public static boolean isFinite(float value) {
		return NEGATIVE_INFINITY < value & value < POSITIVE_INFINITY;
	}

	public static float[][] create(int row, int column) {
		return create(row, column, 0);
	}

	public static float[][] create(int row, int column, float defaultValue) {
		float[][] array = new float[row][column];
		for (int i = 0; i < row; i++) {
			array[i] = create(column, defaultValue);
		}
		return array;
	}

	public static float[] create(int length) {
		return create(length, 0f);
	}

	public static float[] create(int length, float defaultValue) {
		float[] array = new float[length];
		if (defaultValue != 0) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static boolean isNotEmpty(float[] args) {
		return !isEmpty(args);
	}

	public static boolean isEmpty(float[] args) {
		return args != null ? args.length == 0 : true;
	}

	public static boolean notContains(float[] a, float b) {
		return !contains(a, b);
	}

	public static boolean contains(float[] a, float b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(float[] a, float b) {
		return indexOf(a, b, 0, a.length);
	}

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

	public static int lastIndexOf(float[] a, float b) {
		return lastIndexOf(a, b, a.length - 1);
	}

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

	public static float[] concat(float[] left, float[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		float[] floats = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, floats, left.length, right.length);
		return floats;
	}

	public static float[] add(float[] array, float a) {
		Assert.isNull(array, "Source array must not be null.");
		float[] floats = copy(array, array.length + 1);
		floats[floats.length - 1] = a;
		return floats;
	}

	public static float[] remove(float[] array, float a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

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

	public static float[] copy(float[] array) {
		return copy(array, array.length);
	}

	public static float[] copy(float[] array, int length) {
		return copy(array, 0, length);
	}

	public static float[] copy(float[] array, int startIndex, int length) {
		return copy(array, startIndex, length, 0);
	}

	public static float[] copy(float[] array, int startIndex, int length, float defaultValue) {
		float[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(float[] src, int srcFrom, float[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

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

	public static float[] ensureCapacity(float[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static float[] expandCapacity(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	public static float[] expandCapacity(float[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	public static float max(float[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		float max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max(max, array[i]);
		}
		return max;
	}

	public static float min(float[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		float min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min(min, array[i]);
		}
		return min;
	}

	public static float sum(float[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		float sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	public static double avg(float[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	public static String toString(float[] array) {
		return "[" + join(array) + "]";
	}

	public static String join(float[] array) {
		return join(array, ",");
	}

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

	public static String join(float[] left, float[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

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
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		for (float s : right) {
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

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
		float[] result = new float[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (!contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

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
		float[] result = new float[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

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

	public static List<Float> toList(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Float> set = new ArrayList<Float>(array.length);
		for (float a : array) {
			set.add(Float.valueOf(a));
		}
		return set;
	}

	public static void reverse(float[] src) {
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

	public static int hashCode(float arg) {
		return Float.hashCode(arg);
	}

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

	public static Float valueOf(Character c) {
		return valueOf(c, null);
	}

	public static Float valueOf(Character c, Float defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return valueOf(String.valueOf(c), defaultValue);
	}

	public static Float valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static Float valueOf(Boolean b, Float defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Float.valueOf(cast(b));
	}

	public static Float valueOf(String str) {
		return valueOf(str, null);
	}

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

	public static Float[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	public static Float[] valueOf(String[] strings, Float defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		Float[] result = new Float[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	public static float[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

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
		if (pooled == null) {
			String newStr = NumberUtils.read(str);
			if (!NumberUtils.isNumber(newStr)) {
				throw new NumberFormatException("Can not parse string for: " + str);
			}
			cache.put(str, Float.parseFloat(newStr));
			pooled = cache.get(str);
		}
		return pooled.floatValue();
	}

	public static float cast(boolean b) {
		return (b ? 1F : 0F);
	}

	public static float[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static float cast(char value) {
		return (float) value;
	}

	public static float[] casts(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		int i = 0;
		for (char val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static float[] casts(double[] value) {
		return casts(value, true);
	}

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

	public static float cast(Number n) {
		Assert.isNull(n, "Number is required.");
		return n.floatValue();
	}

	public static float[] casts(Number[] array) {
		return casts(array, true);
	}

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

	public static Float[] valueOf(Number[] array) {
		return valueOf(array, null);
	}

	public static Float[] valueOf(Number[] array, Float defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Float[] result = new Float[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	public static Float[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	public static Float[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	public static Float[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	public static Float[] toWrappers(float[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	public static Float[] toWrappers(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	public static Float[] toWrappers(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Float[] result = new Float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Float.valueOf(value[i]);
		}
		return result;
	}

	public static float[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static float[] casts(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static float[] casts(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static float[] casts(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		float[] result = new float[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

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

	public static float toFixed(float value, int scale) {
		return Float.parseFloat(NumberUtils.format(value, scale));
	}

	public static String toPlainString(float value) {
		return BigDecimal.valueOf(value).toPlainString();
	}

	public static String[] toStringArray(float[] args, DecimalFormat df) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = df != null ? df.format(args[i]) : toPlainString(args[i]);
		}
		return array;
	}

	public static boolean isSameLength(float[] left, float[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	public static boolean same(float[] array) {
		return isSerial(array, 0);
	}

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

	public static boolean containsAll(float[] left, float[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (float f : right) {
			if (notContains(left, f)) {
				return false;
			}
		}
		return true;
	}

	public static void leftScroll(float[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n);
		}
	}

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

	public static float max(float a, float b) {
		if (Float.isNaN(a)) {
			return b;
		} else if (Float.isNaN(b)) {
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	public static float min(float a, float b) {
		if (Float.isNaN(a)) {
			return b;
		} else if (Float.isNaN(b)) {
			return a;
		} else {
			return Math.min(a, b);
		}
	}

	public static boolean between(float val, float start, float end) {
		return compare(val, start) >= 0 && compare(val, end) <= 0;
	}

	public static boolean rightIn(float val, float start, float end) {
		return compare(val, start) >= 0 && compare(val, end) < 0;
	}

	public static boolean leftIn(float val, float start, float end) {
		return compare(val, start) > 0 && compare(val, end) <= 0;
	}

	public static boolean in(float val, float start, float end) {
		return compare(val, start) > 0 && compare(val, end) < 0;
	}

	public static void shuffle(float[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	public static void shuffle(float[] src, Random rn) {
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	public static int compare(float a, float b) {
		return Float.compare(a, b);
	}

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
