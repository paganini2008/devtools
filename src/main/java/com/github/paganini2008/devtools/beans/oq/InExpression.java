package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.beans.Property;

/**
 * InExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class InExpression<E, T extends Comparable<T>> implements Expression<E> {

	public InExpression(String propertyName, T[] values) {
		this(propertyName, null, values);
	}

	public InExpression(String propertyName, Class<T> requiredType, T[] values) {
		this.property = Property.forName(propertyName, requiredType);
		this.values = values;
	}

	private final Property<E, T> property;
	private final T[] values;

	public boolean accept(E object) {
		try {
			T result = property.apply(object);
			return Comparables.contains(values, result);
		} catch (RuntimeException e) {
			return false;
		}
	}

}
