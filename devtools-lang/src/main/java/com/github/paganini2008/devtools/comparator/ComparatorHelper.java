package com.github.paganini2008.devtools.comparator;

/**
 * ComparatorHelper
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class ComparatorHelper {

	public static int valueOf(double result) {
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int valueOf(long result) {
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public static <T extends Comparable<T>> int compareTo(T a, T b) {
		if (a != null && b != null) {
			return valueOf(a.compareTo(b));
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}

	public static int compareTo(String a, String b, boolean ignoreCase) {
		if (a != null && b != null) {
			return valueOf(ignoreCase ? a.compareToIgnoreCase(b) : a.compareTo(b));
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}
}
