package com.github.paganini2008.devtools.beans.streaming;

import java.util.function.Predicate;

import com.github.paganini2008.devtools.CompareUtils;

/**
 * 
 * Having
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public abstract class Having {

	public static <E, T extends Comparable<T>> Predicate<Group<E>> eq(Calculation<E, T> calculation, T value) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.eq(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> ne(Calculation<E, T> calculation, T value) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.ne(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> between(Calculation<E, T> calculation, T minValue, T maxValue) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.gte(result, minValue) && CompareUtils.lte(result, maxValue);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> lt(Calculation<E, T> calculation, T value) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.lt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> lte(Calculation<E, T> calculation, T value) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.lte(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> gt(Calculation<E, T> calculation, T value) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.gt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<Group<E>> gte(Calculation<E, T> calculation, T value) {
		return group -> {
			T result = group.summarize(calculation);
			return CompareUtils.gte(result, value);
		};
	}

}
