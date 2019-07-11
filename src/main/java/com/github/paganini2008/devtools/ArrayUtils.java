package com.github.paganini2008.devtools;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ArrayUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class ArrayUtils {

	private ArrayUtils() {
	}

	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	public static final int INDEX_NOT_FOUND = -1;

	public static final int MERGE_SORT_THRESHOLD = 10;

	public static boolean isNotSameLength(Object[] left, Object[] right) {
		return !isSameLength(left, right);
	}

	public static boolean isSameLength(Object[] left, Object[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

	public static int getLength(Object[] array) {
		return array != null ? array.length : 0;
	}

	public static void reverse(Object[] src) {
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

	public static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

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
			if (!contains(result, t)) {
				result[i++] = t;
			}
		}
		for (T t : b) {
			if (!contains(result, t)) {
				result[i++] = t;
			}
		}
		return ensureCapacity(result, i);
	}

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
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), b.length);
		int i = 0;
		for (T t : a) {
			if (!contains(b, t)) {
				result[i++] = t;
			}
		}
		return ensureCapacity(result, i);
	}

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
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), b.length);
		int i = 0;
		for (T t : a) {
			if (contains(b, t)) {
				result[i++] = t;
			}
		}
		return ensureCapacity(result, i);
	}

	public static boolean notContains(Object[] a, Object b) {
		return !contains(a, b);
	}

	public static boolean contains(Object[] a, Object b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(Object[] a, Object b) {
		return indexOf(a, b, 0, a.length);
	}

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

	public static int lastIndexOf(Object[] a, Object b) {
		return lastIndexOf(a, b, a.length - 1);
	}

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

	public static void main(String[] args) {
		String[] arr = { "a", "b", "c", "d", "e", null, null };
		arr = expandCapacity(arr, 3);
		System.out.println(toString(arr));
	}

	public static <T> T[] ensureCapacity(T[] array, int index) {
		Assert.isNull(array);
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static <T> T[] expandCapacity(T[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	public static <T> T[] expandCapacity(T[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, 0, Math.max(length + size, length));
	}

	public static boolean isEmpty(Object[] args) {
		return args != null ? args.length == 0 : true;
	}

	public static boolean isNotEmpty(Object[] args) {
		return !isEmpty(args);
	}

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

	public static <T> T[][] create(Class<?> type, int row, int column) {
		return create(type, row, column, null);
	}

	public static <T> T[][] create(Class<?> type, int row, int column, T defaultValue) {
		Assert.isNull(type, "Array class must not be null.");
		T[][] array = (T[][]) Array.newInstance(type, row, 2);
		for (int i = 0; i < row; i++) {
			array[i] = create(type, column, defaultValue);
		}
		return array;
	}

	public static <T> T[] create(Class<?> type, int length) {
		return create(type, length, null);
	}

	public static <T> T[] create(Class<?> type, int length, T defaultValue) {
		Assert.isNull(type, "Array class must not be null.");
		T[] array = (T[]) Array.newInstance(type, length);
		if (defaultValue != null) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static <T> T[] concat(T[] left, T[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		T[] other = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, other, left.length, right.length);
		return other;
	}

	public static <T> T[] add(T[] array, T a) {
		Assert.isNull(array, "Source array must not be null.");
		T[] other = copy(array, 0, array.length + 1);
		other[other.length - 1] = a;
		return other;
	}

	public static <T> T[] remove(T[] array, T a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

	public static <T> T[] removeAt(T[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index < 0) {
			index = length - Math.abs(index);
		}
		if (index >= 0 && index < length) {
			T[] target = create(array.getClass().getComponentType(), length - 1);
			hardCopy(array, 0, target, 0, index);
			hardCopy(array, index + 1, target, index, length - index - 1);
			return target;
		}
		throw new ArrayIndexOutOfBoundsException("Bad index: " + index);
	}

	public static <T> T[] copy(T[] array) {
		return copy(array, 0);
	}

	public static <T> T[] copy(T[] array, int startIndex) {
		return copy(array, startIndex, array.length);
	}

	public static <T> T[] copy(T[] array, int startIndex, int length) {
		return copy(array, startIndex, length, null);
	}

	public static <T> T[] copy(T[] array, int startIndex, int length, T defaultValue) {
		T[] target = create(array.getClass().getComponentType(), length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static <T> void hardCopy(T[] src, int srcFrom, T[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

	public static String toString(Object[] array) {
		return "[" + join(array) + "]";
	}

	public static String join(Object[] array) {
		return join(array, ",");
	}

	public static String join(Object[] array, String delimiter) {
		int l;
		if (array == null || (l = array.length) == 0) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < l; i++) {
			content.append(ObjectUtils.toString(array[i]));
			if (i != l - 1) {
				content.append(delimiter);
			}
		}
		return content.toString();
	}

	public static String join(Object[] left, Object[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

	public static String join(Object[] left, Object[] right, String conjunction, String delimiter) {
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
			content.append(ObjectUtils.toString(left[i])).append(conjunction).append(ObjectUtils.toString(right[i]));
			if (i != l - 1) {
				content.append(delimiter);
			}
		}
		return content.toString();
	}

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
			if (ObjectUtils.notEquals(o1, o2)) {
				return false;
			}
		}
		return true;
	}

	public static int deepHashCode(Object[] args) {
		Assert.isNull(args, "Source array must not be null.");
		int hash = 0;
		for (int i = 0; i < args.length; i++) {
			hash += ObjectUtils.hashCode(args[i]);
		}
		return hash;
	}

	public static String[] toStringArray(Object[] args) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = ObjectUtils.toString(args);
		}
		return array;
	}

	public static boolean isSubarray(Object[] left, Object[] right) {
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

	public static void leftScroll(Object[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n);
		}
	}

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

	public static Object[][] slice(Object[] src, int n) {
		Assert.isTrue(isEmpty(src), "Empty array.");
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

	public static Object[][] divide(Object[] src, int n) {
		Assert.isTrue(isEmpty(src), "Empty array.");
		Object[][] results = new Object[n][];
		int startIndex = 0;
		int length = src.length / n;
		if (length < 1) {
			length = 1;
		}
		for (int i = 0; i < n - 1; i++) {
			results[i] = copy(src, startIndex, length);
			startIndex += length;
		}
		results[n - 1] = copy(src, startIndex, src.length - startIndex);
		return results;
	}

	public static void shuffle(Object[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	public static void shuffle(Object[] src, Random rn) {
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	public static boolean notContains(char[] a, int b) {
		return !contains(a, b);
	}

	public static boolean contains(char[] a, int b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(char[] a, int b) {
		return indexOf(a, b, 0, a.length);
	}

	public static int indexOf(char[] a, int b, int start, int end) {
		return Chars.indexOf(a, b, start, end);
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
		return Doubles.indexOf(a, b, start, end);
	}

	public static boolean notContains(long[] a, long b) {
		return !contains(a, b);
	}

	public static boolean contains(long[] a, long b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(long[] a, long b) {
		return indexOf(a, b, 0, a.length);
	}

	public static int indexOf(long[] a, long b, int start, int end) {
		return Longs.indexOf(a, b, start, end);
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
		return Floats.indexOf(a, b, start, end);
	}

	public static boolean notContains(int[] a, int b) {
		return !contains(a, b);
	}

	public static boolean contains(int[] a, int b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(int[] a, int b) {
		return indexOf(a, b, 0, a.length);
	}

	public static int indexOf(int[] a, int b, int start, int end) {
		return Ints.indexOf(a, b, start, end);
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
		return Shorts.indexOf(a, b, start, end);
	}

}
