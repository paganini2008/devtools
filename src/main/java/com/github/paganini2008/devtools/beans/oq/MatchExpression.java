package com.github.paganini2008.devtools.beans.oq;

import com.github.paganini2008.devtools.beans.Property;
import com.github.paganini2008.devtools.collection.MatchMode;

/**
 * MatchExpression
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MatchExpression<E> implements Expression<E> {

	public MatchExpression(String propertyName, String substr, MatchMode matchMode) {
		this.property = Property.forName(propertyName, String.class);
		this.substr = substr;
		this.matchMode = matchMode;
	}

	private final Property<E, String> property;
	private final String substr;
	private final MatchMode matchMode;

	public boolean accept(E e) {
		String str = property.apply(e);
		return matchMode.matches(str, substr);
	}

}
