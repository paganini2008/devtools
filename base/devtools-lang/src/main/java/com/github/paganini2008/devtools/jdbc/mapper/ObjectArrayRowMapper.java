package com.github.paganini2008.devtools.jdbc.mapper;

import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;

/**
 * ObjectArrayRowMapper
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ObjectArrayRowMapper extends BasicRowMapper<Object[]> {

	public ObjectArrayRowMapper(TypeHandlerRegistry typeHandlerRegistry) {
		super(typeHandlerRegistry);
	}

	protected Object[] createObject(int columnCount) {
		return new Object[columnCount];
	}

	protected void setValue(Object[] object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue) {
		object[columnIndex - 1] = columnValue;
	}

}
