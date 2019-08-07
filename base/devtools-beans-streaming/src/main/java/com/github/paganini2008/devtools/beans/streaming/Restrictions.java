package com.github.paganini2008.devtools.beans.streaming;

import java.util.Collection;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.CompareUtils;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * 
 * Restrictions
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class Restrictions {

	public static <E> Predicate<E> disjunction() {
		return e -> {
			return false;
		};
	}

	public static <E> Predicate<E> conjunction() {
		return e -> {
			return true;
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> ne(String attributeName, T value) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.ne(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> ne(String attributeName, Class<T> requiredType, T value) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.ne(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> eq(String attributeName, T value) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.eq(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> eq(String attributeName, Class<T> requiredType, T value) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.eq(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> gte(String attributeName, T value) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.gte(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> gte(String attributeName, Class<T> requiredType, T value) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.gte(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> gt(String attributeName, T value) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.gt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> gt(String attributeName, Class<T> requiredType, T value) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.gt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> lte(String attributeName, T value) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.lte(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> lte(String attributeName, Class<T> requiredType, T value) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.lte(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> lt(String attributeName, T value) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.lt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> lt(String attributeName, Class<T> requiredType, T value) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.lt(result, value);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> between(String attributeName, T minValue, T maxValue) {
		return e -> {
			T result = (T) PropertyUtils.getProperty(e, attributeName);
			return CompareUtils.between(result, minValue, maxValue);
		};
	}

	public static <E, T extends Comparable<T>> Predicate<E> between(String attributeName, Class<T> requiredType, T minValue, T maxValue) {
		return e -> {
			T result = BeanUtils.getProperty(e, attributeName, requiredType);
			return CompareUtils.between(result, minValue, maxValue);
		};
	}

	public static <E> Predicate<E> in(String attributeName, Collection<?> values) {
		return e -> {
			Object result = PropertyUtils.getProperty(e, attributeName);
			return values.contains(result);
		};
	}

	public static <E, T> Predicate<E> in(String attributeName, Class<T> requiredType, Collection<?> values) {
		return e -> {
			Object result = BeanUtils.getProperty(e, attributeName, requiredType);
			return values.contains(result);
		};
	}

	public static <E> Predicate<E> notNull(String attributeName) {
		return e -> {
			Object result = PropertyUtils.getProperty(e, attributeName);
			return result != null;
		};
	}

	public static <E> Predicate<E> isNull(String attributeName) {
		return e -> {
			Object result = PropertyUtils.getProperty(e, attributeName);
			return result == null;
		};
	}

	public static <E> Predicate<E> like(String attributeName, String substr, MatchMode matchMode) {
		return e -> {
			String result = (String) PropertyUtils.getProperty(e, attributeName);
			return matchMode.matches(result, substr);
		};
	}

	public static <E> Predicate<E> or(Predicate<E>... predicates) {
		Predicate<E> result = disjunction();
		for (Predicate<E> predicate : predicates) {
			result = result.or(predicate);
		}
		return result;
	}

	public static <E> Predicate<E> and(Predicate<E>... predicates) {
		Predicate<E> result = conjunction();
		for (Predicate<E> predicate : predicates) {
			result = result.and(predicate);
		}
		return result;
	}

}