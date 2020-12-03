package com.github.paganini2008.devtools.beans.streaming;

import java.util.function.Function;

import com.github.paganini2008.devtools.beans.BeanUtils;

/**
 * 
 * Property
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public final class Property<E, T> implements Function<E, T> {

	private final String propertyName;
	private final Class<T> requiredType;

	Property(String propertyName, Class<T> requiredType) {
		this.propertyName = propertyName;
		this.requiredType = requiredType;
	}

	public T apply(E e) {
		return BeanUtils.getProperty(e, propertyName, requiredType);
	}

	public static <E, T> Property<E, T> forName(String propertyName, Class<T> requiredType) {
		return new Property<E, T>(propertyName, requiredType);
	}

}
