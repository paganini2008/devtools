package com.github.paganini2008.devtools;

import static com.github.paganini2008.devtools.ArrayUtils.INDEX_NOT_FOUND;
import static com.github.paganini2008.devtools.ArrayUtils.MERGE_SORT_THRESHOLD;
import static com.github.paganini2008.devtools.ArrayUtils.rn;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Doubles
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Doubles {

	public static final double[] EMPTY_ARRAY = new double[0];

	public static final Double[] EMPTY_OBJECT_ARRAY = new Double[0];

	public static final int BYTES = Double.SIZE / Byte.SIZE;

	private static final LruMap<String, Double> cache = new LruMap<String, Double>(128);

	public static void clearCache() {
		cache.clear();
	}

	private Doubles() {
	}

	public static double[] clone(double[] array) {
		return array != null ? array.clone() : null;
	}

	public static int length(double[] array) {
		return array != null ? array.length : 0;
	}

	public static boolean isFinite(double value) {
		return NEGATIVE_INFINITY < value & value < POSITIVE_INFINITY;
	}

	public static double[][] create(int row, int column) {
		return create(row, column, 0);
	}

	public static double[][] create(int row, int column, double defaultValue) {
		double[][] array = new double[row][column];
		for (int i = 0; i < row; i++) {
			array[i] = create(column, defaultValue);
		}
		return array;
	}

	public static double[] create(int length) {
		return create(length, 0d);
	}

	public static double[] create(int length, double defaultValue) {
		double[] array = new double[length];
		if (defaultValue != 0) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static boolean isNotEmpty(double[] args) {
		return !isEmpty(args);
	}

	public static boolean isEmpty(double[] args) {
		return args != null ? args.length == 0 : true;
	}

	public static boolean notContains(double[] a, double b) {
		return !contains(a, b);
	}

	public static boolean contains(double[] a, double b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(double[] a, double b) {
		return indexOf(a, b, 0, a.length);
	}

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

	public static int lastIndexOf(double[] a, double b) {
		return lastIndexOf(a, b, a.length - 1);
	}

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

	public static double[] concat(double[] left, double[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		double[] doubles = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, doubles, left.length, right.length);
		return doubles;
	}

	public static double[] add(double[] array, double a) {
		Assert.isNull(array, "Source array must not be null.");
		double[] doubles = copy(array, array.length + 1);
		doubles[doubles.length - 1] = a;
		return doubles;
	}

	public static double[] remove(double[] array, double a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

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

	public static double[] copy(double[] array) {
		return copy(array, array.length);
	}

	public static double[] copy(double[] array, int length) {
		return copy(array, 0, length);
	}

	public static double[] copy(double[] array, int startIndex, int length) {
		return copy(array, startIndex, length, 0);
	}

	public static double[] copy(double[] array, int startIndex, int length, double defaultValue) {
		double[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(double[] src, int srcFrom, double[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

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

	public static double[] ensureCapacity(double[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static double[] expandCapacity(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	public static double[] expandCapacity(double[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	public static double max(double a, double b) {
		if (Double.isNaN(a)) {
			return b;
		} else if (Double.isNaN(b)) {
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	public static double min(double a, double b) {
		if (Double.isNaN(a)) {
			return b;
		} else if (Double.isNaN(b)) {
			return a;
		} else {
			return Math.min(a, b);
		}
	}

	public static double max(double[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		double max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max(max, array[i]);
		}
		return max;
	}

	public static double min(double[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		double min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min(min, array[i]);
		}
		return min;
	}

	public static double sum(double[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		double sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	public static double avg(double[] array) {
		double sum = sum(array);
		return (sum / array.length);
	}

	public static String toString(double[] array) {
		return "[" + join(array) + "]";
	}

	public static String join(double[] array) {
		return join(array, ",");
	}

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

	public static String join(double[] left, double[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

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

	public static void asc(double[] array) {
		if (isNotEmpty(array)) {
			double[] aux = (double[]) array.clone();
			mergeSort(aux, array, 0, array.length, true);
		}
	}

	public static void desc(double[] array) {
		if (isNotEmpty(array)) {
			double[] aux = (double[]) array.clone();
			mergeSort(aux, array, 0, array.length, false);
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
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		for (double s : right) {
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

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
		double[] result = new double[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (!contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

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
		double[] result = new double[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

	public static double[] toDoubleArray(List<Double> list) {
		Assert.isNull(list, "Source list must not be null.");
		double[] array = new double[list.size()];
		int i = 0;
		for (Double a : list) {
			if (a != null) {
				array[i++] = a.doubleValue();
			}
		}
		return ensureCapacity(array, i);
	}

	public static double[] toDoubleArray(Set<Double> set) {
		Assert.isNull(set, "Source set must not be null.");
		double[] array = new double[set.size()];
		int i = 0;
		for (Double a : set) {
			if (a != null) {
				array[i++] = a.doubleValue();
			}
		}
		return ensureCapacity(array, i);
	}

	public static List<Double> asList(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Double> set = new ArrayList<Double>(array.length);
		for (double a : array) {
			set.add(Double.valueOf(a));
		}
		return set;
	}

	public static Set<Double> asSet(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		Set<Double> set = new LinkedHashSet<Double>(array.length);
		for (double a : array) {
			set.add(Double.valueOf(a));
		}
		return set;
	}

	public static void reverse(double[] src) {
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

	public static int hashCode(double arg) {
		return ((Double) arg).hashCode();
	}

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

	public static Double valueOf(Character c) {
		return valueOf(c, null);
	}

	public static Double valueOf(Character c, Double defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return Double.valueOf(c.charValue());
	}

	public static Double valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static Double valueOf(Boolean b, Double defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Double.valueOf(cast(b));
	}

	public static Double valueOf(String str) {
		return valueOf(str, null);
	}

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

	public static Double[] valuesOf(String[] strs) {
		return valuesOf(strs, null);
	}

	public static Double[] valuesOf(String[] strs, Double defaultValue) {
		Assert.isNull(strs, "Source array must not be null.");
		Double[] result = new Double[strs.length];
		int i = 0;
		for (String str : strs) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	public static double[] parses(String[] strs) {
		return parses(strs, true);
	}

	public static double[] parses(String[] strs, boolean thrown) {
		Assert.isNull(strs, "Source array must not be null.");
		double[] result = new double[strs.length];
		int i = 0;
		double s;
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
		if (pooled == null) {
			String newStr = NumberUtils.read(str);
			if (!NumberUtils.isNumber(newStr)) {
				throw new NumberFormatException("Can not parse string for: " + str);
			}
			cache.put(str, Double.parseDouble(newStr));
			pooled = cache.get(str);
		}
		return pooled.doubleValue();
	}

	public static double cast(boolean b) {
		return (b ? 1 : 0);
	}

	public static double[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static double cast(char value) {
		return (double) value;
	}

	public static double[] casts(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		int i = 0;
		for (char val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static double cast(Number n) {
		Assert.isNull(n, "Number is required.");
		return n.doubleValue();
	}

	public static double[] casts(Number[] array) {
		return casts(array, true);
	}

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

	public static Double[] toWrappers(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static double[] casts(byte[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static Double[] toWrappers(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static double[] casts(short[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static Double[] toWrappers(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static double[] casts(int[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static Double[] toWrappers(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static double[] casts(long[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static Double[] toWrappers(float[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static Double[] toWrappers(double[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static Double[] toWrappers(char[] value) {
		Assert.isNull(value, "Source array must not be null.");
		Double[] result = new Double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = Double.valueOf(value[i]);
		}
		return result;
	}

	public static double[] casts(float[] value) {
		Assert.isNull(value, "Source array must not be null.");
		double[] result = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = value[i];
		}
		return result;
	}

	public static Double valueOf(Number n) {
		return valueOf(n, null);
	}

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

	public static Double[] valuesOf(Number[] array) {
		return valuesOf(array, null);
	}

	public static Double[] valuesOf(Number[] array, Double defaultValue) {
		Assert.isNull(array, "Source array must not be null.");
		Double[] result = new Double[array.length];
		int i = 0;
		for (Number arg : array) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	public static int deepHashCode(double[] args) {
		Assert.isNull(args, "Source array must not be null.");
		int hash = 0;
		for (int i = 0; i < args.length; i++) {
			hash += hashCode(args[i]);
		}
		return hash;
	}

	public static double toFixed(double value, int scale) {
		return Double.parseDouble(NumberUtils.format(value, scale));
	}

	public static String toPlainString(double value) {
		return BigDecimal.valueOf(value).toPlainString();
	}

	public static String[] toStringArray(double[] args) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = toPlainString(args[i]);
		}
		return array;
	}

	public static boolean isSameLength(double[] left, double[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	public static boolean same(double[] array) {
		return isSequentially(array, 0);
	}

	public static boolean isSequentially(double[] array, double n) {
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

	public static boolean containsAll(double[] left, double[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (double d : right) {
			if (notContains(left, d)) {
				return false;
			}
		}
		return true;
	}

	public static void leftScroll(double[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n);
		}
	}

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

	public static boolean between(double val, double start, double end) {
		return compare(val, start) >= 0 && compare(val, end) <= 0;
	}

	public static boolean in(double val, double start, double end) {
		return compare(val, start) > 0 && compare(val, end) < 0;
	}

	public static boolean rightIn(double val, double start, double end) {
		return compare(val, start) >= 0 && compare(val, end) < 0;
	}

	public static boolean leftIn(double val, double start, double end) {
		return compare(val, start) > 0 && compare(val, end) <= 0;
	}

	public static void shuffle(double[] src) {
		shuffle(src, rn);
	}

	public static void shuffle(double[] src, Random rn) {
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	public static int compare(double a, double b) {
		return Double.compare(a, b);
	}

	public static Comparator<double[]> lexicographicalComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	private enum LexicographicalComparator implements Comparator<double[]> {
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
