package com.github.paganini2008.devtools.db4j.mapper;

import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * 
 * BeanPropertyRowMapper
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class BeanPropertyRowMapper<T> extends AbstractRowMapper<T> {

	private final Class<T> elementType;

	public BeanPropertyRowMapper(TypeHandlerRegistry typeHandlerRegistry, Class<T> elementType) {
		super(typeHandlerRegistry);
		this.elementType = elementType;
	}

	protected T createObject(int columnCount) {
		try {
			return ConstructorUtils.invokeConstructor(elementType, (Object[]) null);
		} catch (Exception e) {
			throw new RowMapperException(e);
		}
	}

	protected void setValue(T object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType, Object columnValue) {
		PropertyUtils.setProperty(object, columnName, columnValue);
	}

}
