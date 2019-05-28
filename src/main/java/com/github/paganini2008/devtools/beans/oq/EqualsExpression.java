package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.Property;

/**
 * EqualsExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class EqualsExpression<E> implements Expression<E> {

	private final Property<E, String> property;
	private final String value;
	private final boolean ignoreCase;

	public EqualsExpression(String propertyName, String value) {
		this(propertyName, value, false);
	}

	public EqualsExpression(String propertyName, String value, boolean ignoreCase) {
		this.property = Property.forName(propertyName, String.class);
		this.value = value;
		this.ignoreCase = ignoreCase;
	}

	public boolean accept(E entity) {
		String str = property.apply(entity);
		return ignoreCase ? StringUtils.equalsIgnoreCase(str, value) : StringUtils.equals(str, value);
	}

}
