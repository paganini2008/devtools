package com.github.paganini2008.devtools;

import static com.github.paganini2008.devtools.ArrayUtils.INDEX_NOT_FOUND;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * Booleans
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class Booleans {

	private static final List<String> trueOrFalses = ListUtils
			.create(new String[] { "TRUE", "FALSE", "true", "false", "YES", "NO", "yes", "no", "ON", "OFF", "on", "off", "1", "0" });

	private static final List<Character> trueOrFalseChars = ListUtils
			.create(new Character[] { 'T', 'F', 't', 'f', 'Y', 'N', 'y', 'n', '1', '0' });

	public static final boolean[] EMPTY_ARRAY = new boolean[0];

	public static final Boolean[] EMPTY_OBJECT_ARRAY = new Boolean[0];

	private Booleans() {
	}

	public static void addTrueAndFalse(char trueChar, char falseChar) {
		trueOrFalseChars.add(trueChar);
		trueOrFalseChars.add(falseChar);
	}

	public static void addTrueAndFalse(String trueString, String falseString) {
		trueOrFalses.add(trueString);
		trueOrFalses.add(falseString);
	}

	public static boolean[] clone(boolean[] array) {
		return array != null ? array.clone() : null;
	}

	public static int length(boolean[] array) {
		return array != null ? array.length : 0;
	}

	public static boolean[][] create(int yLength, int xLength) {
		return create(yLength, xLength, false);
	}

	public static boolean[][] create(int yLength, int xLength, boolean defaultValue) {
		boolean[][] array = new boolean[yLength][xLength];
		if (defaultValue) {
			for (int i = 0; i < yLength; i++) {
				array[i] = create(xLength, defaultValue);
			}
		}
		return array;
	}

	public static boolean[] create(int length) {
		return create(length, false);
	}

	public static boolean[] create(int length, boolean defaultValue) {
		boolean[] array = new boolean[length];
		if (defaultValue) {
			for (int i = 0; i < length; i++) {
				array[i] = defaultValue;
			}
		}
		return array;
	}

	public static boolean isNotEmpty(boolean[] args) {
		return !isEmpty(args);
	}

	public static boolean isEmpty(boolean[] args) {
		return args != null ? args.length == 0 : true;
	}

	public static boolean notContains(boolean[] a, boolean b) {
		return !contains(a, b);
	}

	public static boolean contains(boolean[] a, boolean b) {
		return indexOf(a, b) != INDEX_NOT_FOUND;
	}

	public static int indexOf(boolean[] a, boolean b) {
		return indexOf(a, b, 0, a.length);
	}

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

	public static int lastIndexOf(boolean[] a, boolean b) {
		return lastIndexOf(a, b, a.length - 1);
	}

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

	public static boolean[] concat(boolean[] left, boolean[] right) {
		Assert.isNull(left, "Left array must not be null.");
		Assert.isNull(right, "Right array must not be null.");
		boolean[] booleans = copy(left, 0, left.length + right.length);
		hardCopy(right, 0, booleans, left.length, right.length);
		return booleans;
	}

	public static boolean[] add(boolean[] array, boolean a) {
		Assert.isNull(array, "Source array must not be null.");
		boolean[] booleans = copy(array, array.length + 1);
		booleans[booleans.length - 1] = a;
		return booleans;
	}

	public static boolean[] remove(boolean[] array, boolean a) {
		int index = indexOf(array, a);
		return index != INDEX_NOT_FOUND ? removeAt(array, index) : array;
	}

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

	public static boolean[] copy(boolean[] array) {
		return copy(array, array.length);
	}

	public static boolean[] copy(boolean[] array, int length) {
		return copy(array, 0, length);
	}

	public static boolean[] copy(boolean[] array, int startIndex, int length) {
		return copy(array, startIndex, length, false);
	}

	public static boolean[] copy(boolean[] array, int startIndex, int length, boolean defaultValue) {
		boolean[] target = create(length, defaultValue);
		hardCopy(array, startIndex, target, 0, length);
		return target;
	}

	private static void hardCopy(boolean[] src, int srcFrom, boolean[] dest, int destFrom, int length) {
		System.arraycopy(src, srcFrom, dest, destFrom, Math.min(src.length, length));
	}

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

	public static boolean[] ensureCapacity(boolean[] array, int index) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		if (index != length) {
			return copy(array, 0, Math.min(index, length));
		}
		return array;
	}

	public static boolean[] expandCapacity(boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		return expandCapacity(array, array.length);
	}

	public static boolean[] expandCapacity(boolean[] array, int size) {
		Assert.isNull(array, "Source array must not be null.");
		int length = array.length;
		return copy(array, Math.max(length + size, length));
	}

	public static String toString(boolean[] array, String delim) {
		return "[" + join(array) + "]";
	}

	public static String join(boolean[] array) {
		return join(array, ",");
	}

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

	public static String join(boolean[] left, boolean[] right, String delimiter) {
		return join(left, right, delimiter, delimiter);
	}

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

	public static boolean[] toByteArray(List<Boolean> list) {
		Assert.isNull(list, "Source list must not be null.");
		boolean[] array = new boolean[list.size()];
		int i = 0;
		for (Boolean a : list) {
			if (a != null) {
				array[i++] = a.booleanValue();
			}
		}
		return ensureCapacity(array, i);
	}

	public static boolean[] toByteArray(Set<Boolean> set) {
		Assert.isNull(set, "Source set must not be null.");
		boolean[] array = new boolean[set.size()];
		int i = 0;
		for (Boolean a : set) {
			if (a != null) {
				array[i++] = a.booleanValue();
			}
		}
		return ensureCapacity(array, i);
	}

	public static List<Boolean> asList(boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		List<Boolean> set = new ArrayList<Boolean>(array.length);
		for (boolean a : array) {
			set.add(Boolean.valueOf(a));
		}
		return set;
	}

	public static Set<Boolean> asSet(boolean[] array) {
		Assert.isNull(array, "Source array must not be null.");
		Set<Boolean> set = new LinkedHashSet<Boolean>(array.length);
		for (boolean a : array) {
			set.add(Boolean.valueOf(a));
		}
		return set;
	}

	public static int hashCode(boolean arg) {
		return arg ? 1231 : 1237;
	}

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

	public static int deepHashCode(boolean[] args) {
		int hash = 0;
		for (int i = 0; i < args.length; i++) {
			hash += hashCode(args[i]);
		}
		return hash;
	}

	private static int indexFor(String arg) {
		return trueOrFalses.indexOf(arg);
	}

	private static int indexFor(char arg) {
		return trueOrFalseChars.indexOf(arg);
	}

	public static Boolean valueOf(Character arg) {
		return valueOf(arg, null);
	}

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

	public static Boolean valueOf(String arg) {
		return valueOf(arg, null);
	}

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

	public static Boolean[] valuesOf(String[] args) {
		return valuesOf(args, null);
	}

	public static Boolean[] valuesOf(String[] args, Boolean defaultValue) {
		Boolean[] result = new Boolean[args.length];
		int i = 0;
		for (String arg : args) {
			result[i++] = valueOf(arg, defaultValue);
		}
		return result;
	}

	public static boolean parse(char arg) {
		int i;
		if ((i = indexFor(arg)) != -1) {
			return Ints.isEven(i);
		}
		throw new BooleanFormatException(arg);
	}

	public static boolean[] parses(char[] args) {
		return parses(args, true);
	}

	public static boolean[] parses(char[] args, boolean thrown) {
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

	public static boolean parse(String arg) {
		Assert.hasNoText(arg);
		int i;
		if ((i = indexFor(arg)) != -1) {
			return Ints.isEven(i);
		}
		throw new BooleanFormatException(arg);
	}

	public static boolean[] parses(String[] args) {
		return parses(args, true);
	}

	public static boolean[] parses(String[] args, boolean thrown) {
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

	public static Boolean valueOf(Number n) {
		return valueOf(n, null);
	}

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

	public static String toStringYesNo(Boolean b) {
		return toStringYesNo(b, "no");
	}

	public static String toStringYesNo(Boolean b, String defaultValue) {
		return b != null ? b ? "yes" : "no" : defaultValue;
	}

	public static String toStringOnOff(Boolean b) {
		return toStringOnOff(b, "off");
	}

	public static String toStringOnOff(Boolean b, String defaultValue) {
		return b != null ? b ? "on" : "off" : defaultValue;
	}

	public static String toString(Boolean b) {
		return toString(b, "false");
	}

	public static String toString(Boolean b, String defaultValue) {
		return b != null ? b ? "true" : "false" : defaultValue;
	}

	public static String[] toStringArray(boolean[] args) {
		int l = args.length;
		String[] array = new String[l];
		for (int i = 0; i < l; i++) {
			array[i] = String.valueOf(args[i]);
		}
		return array;
	}

	public static boolean isSameLength(boolean[] left, boolean[] right) {
		if (left == null) {
			return right != null ? right.length == 0 : true;
		} else if (right == null) {
			return left != null ? left.length == 0 : true;
		} else {
			return left.length == right.length;
		}
	}

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

	public static boolean containsAll(boolean[] left, boolean[] right) {
		if (isEmpty(left) || isEmpty(right) || left.length < right.length) {
			return false;
		}
		for (boolean b : right) {
			if (notContains(left, b)) {
				return false;
			}
		}
		return true;
	}

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

	public static Comparator<boolean[]> lexicographicalComparator() {
		return LexicographicalComparator.INSTANCE;
	}

	public static int compare(boolean a, boolean b) {
		return (a == b) ? 0 : (a ? 1 : -1);
	}

	private enum LexicographicalComparator implements Comparator<boolean[]> {

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
