package com.github.paganini2008.devtools.beans.oq;

/**
 * NotExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class NotExpression<E> extends LogicExpression<E> {

	public NotExpression(Expression<E> expression) {
		this.expression = expression;
	}

	private Expression<E> expression;

	public boolean accept(E e) {
		return !expression.accept(e);
	}

}
