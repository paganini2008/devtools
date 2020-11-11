package com.github.paganini2008.devtools;

/**
 * 
 * Comparables
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class Comparables {

	public static <T> T nullOrMax(T leftValue, T rightValue) {
		return leftValue == null ? rightValue : leftValue;
	}

	public static <T> T nullOrMin(T leftValue, T rightValue) {
		return leftValue == null ? leftValue : rightValue;
	}

	public static <T extends Comparable<T>> T getOrDefault(T left, T right) {
		return getOrDefault(left, right, null);
	}

	public static <T extends Comparable<T>> T getOrDefault(T left, T right, T defaultValue) {
		if (compareTo(left, right) == 0) {
			return defaultValue;
		}
		return left;
	}

	public static <T extends Comparable<T>> T minOrDefault(T left, T right, T defaultValue) {
		if (compareTo(left, right) == 0) {
			return defaultValue;
		}
		return min(left, defaultValue);
	}

	public static <T extends Comparable<T>> T maxOrDefault(T left, T right, T defaultValue) {
		if (compareTo(left, right) == 0) {
			return defaultValue;
		}
		return max(left, defaultValue);
	}

	public static <T extends Comparable<T>> T max(T[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return null;
		}
		T max = array[0];
		for (T t : array) {
			max = max(max, t);
		}
		return max;
	}

	public static <T extends Comparable<T>> T min(T[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return null;
		}
		T min = array[0];
		for (T t : array) {
			min = min(min, t);
		}
		return min;
	}

	public static <T extends Comparable<T>> T max(T a, T b) {
		return compareTo(a, b) >= 0 ? a : b;
	}

	public static <T extends Comparable<T>> T min(T a, T b) {
		return compareTo(a, b) <= 0 ? a : b;
	}

	public static <T extends Comparable<T>> boolean between(T value, T minValue, T maxValue) {
		return gte(value, minValue) && lte(value, maxValue);
	}

	public static <T extends Comparable<T>> boolean ne(T left, T right) {
		return compareTo(left, right) != 0;
	}

	public static <T extends Comparable<T>> boolean eq(T left, T right) {
		return compareTo(left, right) == 0;
	}

	public static <T extends Comparable<T>> boolean gte(T left, T right) {
		return compareTo(left, right) >= 0;
	}

	public static <T extends Comparable<T>> boolean gt(T left, T right) {
		return compareTo(left, right) > 0;
	}

	public static <T extends Comparable<T>> boolean lte(T left, T right) {
		return compareTo(left, right) <= 0;
	}

	public static <T extends Comparable<T>> boolean lt(T left, T right) {
		return compareTo(left, right) < 0;
	}

	public static <T extends Comparable<T>> int compareTo(T left, T right) {
		if (left == null && right != null) {
			return -1;
		} else if (left != null && right == null) {
			return 1;
		} else if (left != null && right != null) {
			return compareResult(left.compareTo(right));
		}
		return 0;
	}

	private static <T extends Comparable<T>> int compareResult(int result) {
		if (result < 0) {
			return -1;
		}
		if (result > 0) {
			return 1;
		}
		return 0;
	}

}
