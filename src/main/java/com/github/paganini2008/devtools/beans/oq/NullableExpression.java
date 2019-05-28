package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.beans.Property;

/**
 * NullableExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class NullableExpression<E> implements Expression<E> {

	public NullableExpression(String propertyName, boolean yes) {
		this.property = Property.forName(propertyName);
		this.yes = yes;
	}

	private final Property<E, Object> property;
	private final boolean yes;

	public boolean accept(E e) {
		Object result = property.apply(e);
		return yes ? result == null : result != null;
	}

}
