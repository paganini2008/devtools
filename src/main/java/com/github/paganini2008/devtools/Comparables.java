package com.github.paganini2008.devtools;

import java.util.Collection;

public class Comparables {

	private Comparables() {
	}
	
	public static void main(String[] args) {
	}

	public static <T extends Comparable<T>> T max(T a, T b) {
		return compareTo(a, b) >= 0 ? a : b;
	}

	public static <T extends Comparable<T>> T min(T a, T b) {
		return compareTo(a, b) <= 0 ? a : b;
	}

	public static <T extends Comparable<T>> int compareTo(T a, T b) {
		if (a != null && b != null) {
			if (a.getClass() == b.getClass()) {
				return a.compareTo(b);
			}
			throw new IllegalArgumentException(
					"Mismatched type for " + a.getClass().getName() + " and " + b.getClass().getName());
		}
		if (a != null && b == null) {
			return 1;
		}
		if (a == null && b != null) {
			return -1;
		}
		return 0;
	}

	public static <T extends Comparable<T>> boolean contains(T[] array, T value) {
		if (array != null) {
			for (T t : array) {
				if (compareTo(t, value) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T extends Comparable<T>> boolean contains(Collection<T> c, T value) {
		if (c != null) {
			for (T t : c) {
				if (compareTo(t, value) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T extends Comparable<T>> boolean notIn(T value, T left, T right) {
		return !in(value, left, right);
	}

	public static <T extends Comparable<T>> boolean in(T value, T left, T right) {
		if (value.getClass() == left.getClass() && left.getClass() == right.getClass()) {
			return value.compareTo(left) > 0 && value.compareTo(left) < 0;
		}
		throw new ClassCastException(value.getClass().getName() + " and " + left.getClass().getName() + " and "
				+ right.getClass().getName() + " are not same type.");
	}

	public static <T extends Comparable<T>> boolean between(T value, T left, T right) {
		if (value.getClass() == left.getClass() && left.getClass() == right.getClass()) {
			return value.compareTo(left) >= 0 && value.compareTo(left) <= 0;
		}
		throw new ClassCastException(value.getClass().getName() + " and " + left.getClass().getName() + " and "
				+ right.getClass().getName() + " are not same type.");
	}

	public static <T extends Comparable<T>> boolean notBetween(T value, T left, T right) {
		return !between(value, left, right);
	}

	public static <T extends Comparable<T>> T max(T[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		T max = array[0];
		for (T t : array) {
			max = max(max, t);
		}
		return max;
	}

	public static <T extends Comparable<T>> T min(T[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		T min = array[0];
		for (T t : array) {
			min = min(min, t);
		}
		return min;
	}

}
