package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.beans.Property;

/**
 * EqExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class EqExpression<E, T extends Comparable<T>> implements Expression<E> {

	private final Property<E, T> property;
	private final T value;

	public EqExpression(String propertyName, T value) {
		this.property = Property.forName(propertyName, value.getClass());
		this.value = value;
	}

	public boolean accept(E object) {
		T result = property.apply(object);
		try {
			return Comparables.compareTo(result, value) == 0;
		} catch (RuntimeException e) {
			return false;
		}
	}
}
