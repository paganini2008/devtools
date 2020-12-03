package com.github.paganini2008.devtools.db4j.mapper;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * ObjectArrayRowMapper
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ObjectArrayRowMapper extends AbstractRowMapper<Object[]> {

	protected Object[] createObject(int columnCount) {
		return new Object[columnCount];
	}

	protected void setValue(Object[] object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue) {
		object[columnIndex - 1] = columnValue;
	}

}
