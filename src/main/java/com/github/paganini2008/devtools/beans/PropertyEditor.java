package com.github.paganini2008.devtools.beans;

/**
 * PropertyEditor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface PropertyEditor<E> {
	
	<T> T getValue(E object, String propertyName);

	<T> T getValue(E object, String propertyName, T defaultValue);
}
