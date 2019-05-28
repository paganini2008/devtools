package com.github.paganini2008.devtools.beans.oq;

/**
 * AndExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class AndExpression<E> extends LogicExpression<E> {

	private final Expression<E> leftExpression;
	private final Expression<E> rightExpression;

	public AndExpression(Expression<E> leftExpression, Expression<E> rightExpression) {
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
	}

	public boolean accept(E e) {
		return leftExpression.accept(e) && rightExpression.accept(e);
	}

}
