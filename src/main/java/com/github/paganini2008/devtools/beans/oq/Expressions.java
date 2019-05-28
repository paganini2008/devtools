package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.collection.MatchMode;

/**
 * Expressions
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Expressions {

	private Expressions() {
	}

	public static <E> Expression<E> isNull(String property) {
		return new NullableExpression<E>(property, true);
	}

	public static <E> Expression<E> notNull(String property) {
		return new NullableExpression<E>(property, false);
	}

	public static <E, T extends Comparable<T>> Expression<E> eq(String property, T value) {
		return new EqExpression<E, T>(property, value);
	}

	public static <E, T extends Comparable<T>> Expression<E> ne(String property, T value) {
		return new NeExpression<E, T>(property, value);
	}

	public static <E, T extends Comparable<T>> Expression<E> in(String property, T[] values) {
		return new InExpression<E, T>(property, values);
	}

	public static <E> Expression<E> equals(String property, String value) {
		return new EqualsExpression<E>(property, value);
	}

	public static <E> Expression<E> match(String property, String substr, MatchMode matchMode) {
		return new MatchExpression<E>(property, substr, matchMode);
	}

	public static <E, T extends Comparable<T>> Expression<E> gt(String property, T value) {
		return new GtExpression<E, T>(property, value);
	}

	public static <E, T extends Comparable<T>> Expression<E> lt(String property, T value) {
		return new LtExpression<E, T>(property, value);
	}

	public static <E, T extends Comparable<T>> Expression<E> gte(String property, T value) {
		return new GteExpression<E, T>(property, value);
	}

	public static <E, T extends Comparable<T>> Expression<E> lte(String property, T value) {
		return new LteExpression<E, T>(property, value);
	}

	public static <E> LogicExpression<E> and(Expression<E> leftExpression, Expression<E> rightExpression) {
		return new AndExpression<E>(leftExpression, rightExpression);
	}

	public static <E> LogicExpression<E> or(Expression<E> leftExpression, Expression<E> rightExpression) {
		return new OrExpression<E>(leftExpression, rightExpression);
	}

	public static <E> LogicExpression<E> not(Expression<E> leftExpression) {
		return new NotExpression<E>(leftExpression);
	}

}
