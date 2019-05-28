package com.github.paganini2008.devtools.beans.oq;

/**
 * LogicExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class LogicExpression<E> implements Expression<E> {

	public LogicExpression<E> and(Expression<E> expression) {
		return new AndExpression<E>(this, expression);
	}

	public LogicExpression<E> or(Expression<E> expression) {
		return new OrExpression<E>(this, expression);
	}

	public LogicExpression<E> not() {
		return new NotExpression<E>(this);
	}
}
