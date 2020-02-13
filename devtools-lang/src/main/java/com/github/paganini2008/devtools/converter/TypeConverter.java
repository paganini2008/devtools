package com.github.paganini2008.devtools.converter;

/**
 * TypeConverter. A dataType convert to ananother dataType.
 * 
 * @author Fred Feng
 * @created 2016-01
 * @revised 2019-05
 * @version 1.0
 */
public interface TypeConverter {

	<T> void register(Class<T> javaType, BaseConverter<T> converter);

	void remove(Class<?> javaType);

	boolean contains(Class<?> javaType);

	<T> BaseConverter<T> lookup(Class<T> javaType);

	<T> T convert(Object value, Class<T> requiredType, T defaultValue);

}
