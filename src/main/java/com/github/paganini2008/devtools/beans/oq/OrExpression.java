package com.github.paganini2008.devtools.beans.oq;

/**
 * OrExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class OrExpression<E> extends LogicExpression<E> {

	public OrExpression(Expression<E> leftExpression, Expression<E> rightExpression) {
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
	}

	private final Expression<E> leftExpression;
	private final Expression<E> rightExpression;

	public boolean accept(E e) {
		return leftExpression.accept(e) || rightExpression.accept(e);
	}

}
