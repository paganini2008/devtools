package com.github.paganini2008.devtools.jdbc.mapper;

import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * ObjectRowMapper
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ObjectRowMapper<T> extends BasicRowMapper<T> {

	private final Class<T> objectClass;
	private final TypeConverter typeConverter;

	public ObjectRowMapper(TypeHandlerRegistry typeHandlerRegistry, Class<T> objectClass, TypeConverter typeConverter) {
		super(typeHandlerRegistry);
		this.objectClass = objectClass;
		this.typeConverter = typeConverter;
	}

	protected T createObject(int columnCount) {
		try {
			return ConstructorUtils.invokeConstructor(objectClass, null);
		} catch (Exception e) {
			throw new RowMapperException(e);
		}
	}

	protected void setValue(T object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue) {
		PropertyUtils.setProperty(object, columnName, columnValue, typeConverter);
	}

}
