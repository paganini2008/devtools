package com.github.paganini2008.devtools.db4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * 
 * ColumnNameRowMapper
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class ColumnNameRowMapper<T> implements RowMapper<T> {

	private final TypeHandlerRegistry typeHandlerRegistry;
	private final String columnName;
	private final JdbcType jdbcType;
	private final Class<T> requiredType;

	public ColumnNameRowMapper(TypeHandlerRegistry typeHandlerRegistry, String columnName, JdbcType jdbcType, Class<T> requiredType) {
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.columnName = columnName;
		this.jdbcType = jdbcType;
		if (requiredType == null) {
			throw new IllegalArgumentException("The requiredType must not be null.");
		}
		this.requiredType = requiredType;
	}

	public T mapRow(int rownum, ResultSet rs) throws SQLException {
		Object result = getColumnValue(rs, columnName, jdbcType);
		return requiredType.cast(result);
	}

	protected Object getColumnValue(ResultSet rs, String columnName, JdbcType jdbcType) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(null, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnName);
	}

}
