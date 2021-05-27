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
 * 
 * Chars
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Chars {

	public static final char CR = '\r';

	public static final char LF = '\n';

	private static final int MERGE_SORT_THRESHOLD = 10;

	public static final char[] EMPTY_ARRAY = new char[0];

	public static final Character[] EMPTY_OBJECT_ARRAY = new Character[0];

	public static char[] clone(char[] array) {
		return array != null ? array.clone() : null;
	}

	public static int length(char[] array) {
		return array != null ? array.length : 0;
	}

	public static char[][] create(int yLength, int xLength, char defaultValue) {
		char[][] array = new char[yLength][xLength];
		for (int i = 0; i < yLength; i++) {
			array[i] = create(xLength, defaultValue);
		}
		return array;
	}

	public static char[] create(int length, char defaultValue) {
		char[] array = new char[length];
		if (defaultValue != Character.MIN_VALUE) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static boolean isNotEmpty(char[] args) {
		return !isEmpty(args);
	}

	public static boolean isEmpty(char[] args) {
		return args != null ? args.length == 0 : true;
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

	public static int lastIndexOf(char[] a, char b) {
		return lastIndexOf(a, b, a.length - 1);
	}

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

	public static char[] concat(char[] left, char[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		char[] chars = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, chars, left.length, right.length);
		return chars;
	}

	public static char[] add(char[] array, char a) {
		Assert.isNull(array, "Source array must not be null.");
		char[] chars = copy(array, array.length + 1);
		chars[chars.length - 1] = a;
		return chars;
	}

	public static char[] remove(char[] array, char a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

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

	public static char[] copy(char[] array) {
		return copy(array, array.length);
	}

	public static char[] copy(char[] array, int length) {
		return copy(array, 0, length);
	}

	public static char[] copy(char[] array, int startIndex, int length) {
		return copy(array, startIndex, length, Character.MIN_VALUE);
	}

	public static char[] copy(char[] array, int startIndex, int length, char defaultValue) {
		char[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(char[] src, int srcFrom, char[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

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

	public static char[] ensureCapacity(char[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static char[] expandCapacity(char[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	public static char[] expandCapacity(char[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	public static String toString(char[] array) {
		return "[" + join(array) + "]";
	}

	public static String join(char[] array) {
		return join(array, ",");
	}

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

	public static String join(char[] left, char[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

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

	public static void asc(char[] array) {
		if (isNotEmpty(array)) {
			char[] aux = (char[]) array.clone();
			mergeSort(aux, array, 0, array.length, true);
		}
	}

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
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		for (char s : right) {
			if (!contains(total, s)) {
				total[i++] = s;
			}
		}
		return ensureCapacity(total, i);
	}

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
		char[] result = new char[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (!contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

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
		char[] result = new char[right.length];
		int i = 0;
		for (; i < left.length; i++) {
			if (contains(right, left[i])) {
				result[i++] = left[i];
			}
		}
		return ensureCapacity(result, i);
	}

	public static char max(char[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		char max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max >= array[i] ? max : array[i];
		}
		return max;
	}

	public static char min(char[] array) {
		Assert.isTrue(isEmpty(array), "Empty array.");
		char min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min <= array[i] ? min : array[i];
		}
		return min;
	}

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

	public static List<Character> toList(char[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Character> set = new ArrayList<Character>(array.length);
		for (char a : array) {
			set.add(Character.valueOf(a));
		}
		return set;
	}

	public static void reverse(char[] src) {
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

	public static char cast(boolean b) {
		return b ? 'Y' : 'N';
	}

	public static char[] casts(boolean[] value) {
		Assert.isNull(value, "Source array must not be null.");
		char[] result = new char[value.length];
		int i = 0;
		for (boolean val : value) {
			result[i++] = cast(val);
		}
		return result;
	}

	public static char cast(int value) {
		if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
			throw CharValueOverflowException.causedByChar(value);
		}
		return (char) value;
	}

	public static char[] casts(int[] value) {
		return casts(value, true);
	}

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

	public static Character valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static Character valueOf(Boolean b, Character defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return Character.valueOf(cast(b));
	}

	public static Character valueOf(Integer i) {
		return valueOf(i, null);
	}

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

	public static List<Character> toCharacterList(CharSequence value) {
		List<Character> list = new ArrayList<Character>();
		for (int i = 0; i < value.length(); i++) {
			list.add(Character.valueOf(value.charAt(i)));
		}
		return list;
	}

	public static Character[] toCharacterArray(CharSequence value) {
		int length = value.length();
		Character[] result = new Character[length];
		for (int i = 0; i < length; i++) {
			result[i++] = Character.valueOf(value.charAt(i));
		}
		return result;
	}

	public static int hashCode(char arg) {
		return Character.hashCode(arg);
	}

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

	public static Character valueOf(String str) {
		return valueOf(str, null);
	}

	public static Character valueOf(String str, Character defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		return Character.valueOf(str.charAt(0));
	}

	public static boolean isAlpha(char ch) {
		return isUpper(ch) || isLower(ch);
	}

	public static boolean isPrintable(char ch) {
		return ch >= 32 && ch < 127;
	}

	public static boolean isControl(char ch) {
		return ch < 32 || ch == 127;
	}

	public static boolean isAscii(char ch) {
		return ch < 128;
	}

	public static boolean isUpper(char ch) {
		return ch >= 'A' && ch <= 'Z';
	}

	public static boolean isLower(char ch) {
		return ch >= 'a' && ch <= 'z';
	}

	public static boolean isNumber(char ch) {
		return ch >= '0' && ch <= '9';
	}

	public static boolean isAlphaNumber(char ch) {
		return isAlpha(ch) || isNumber(ch);
	}

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

	public static String[] toStringArray(char[] args) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = String.valueOf(args[i]);
		}
		return array;
	}

	public static boolean isSameLength(char[] left, char[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

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

	public static boolean containsAll(char[] left, char[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (char c : right) {
			if (notContains(left, c)) {
				return false;
			}
		}
		return true;
	}

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

	public static void leftScroll(char[] src, int n) {
		if (isNotEmpty(src) && n > 0) {
			int l = src.length;
			rightScroll(src, l - n);
		}
	}

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

	public static void shuffle(char[] src) {
		shuffle(src, ThreadLocalRandom.current());
	}

	public static void shuffle(char[] src, Random rn) {
		int i = src.length;
		for (; i > 1; i--) {
			swap(src, i - 1, rn.nextInt(i));
		}
	}

	public static Comparator<char[]> defaultComparator() {
		return LexicographicalComparator.INSTANCE;
	}

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
