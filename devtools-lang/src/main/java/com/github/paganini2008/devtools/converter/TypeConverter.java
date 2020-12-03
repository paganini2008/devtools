package com.github.paganini2008.devtools.converter;

/**
 * 
 * TypeConverter 
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface TypeConverter {

	<T> void registerType(Class<T> javaType, BasicConverter<T> converter);

	void removeType(Class<?> javaType);

	boolean hasType(Class<?> javaType);

	<T> BasicConverter<T> lookupType(Class<T> javaType);

	<T> T convertValue(Object value, Class<T> requiredType, T defaultValue);

}
