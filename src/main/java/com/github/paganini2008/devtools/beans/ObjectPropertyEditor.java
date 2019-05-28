package com.github.paganini2008.devtools.beans;

import com.github.paganini2008.devtools.reflection.FieldUtils;

/**
 * ObjectPropertyEditor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ObjectPropertyEditor<E> implements PropertyEditor<E> {

	public <T> T getValue(E object, String propertyName) {
		return getValue(object, propertyName, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(E object, String propertyName, T defaultValue) {
		try {
			return (T) PropertyUtils.getProperty(object, propertyName);
		} catch (RuntimeException e) {
			return (T) FieldUtils.readField(object, propertyName);
		}
	}

}
