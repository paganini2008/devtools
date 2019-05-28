package com.github.paganini2008.devtools.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.jdbc.type.TypeHandler;

public class ColumnNameRowMapper<T> implements RowMapper<T> {

	private final TypeHandlerRegistry typeHandlerRegistry;
	private final String columnName;
	private final JdbcType jdbcType;
	private final Class<T> requiredType;
	private final TypeConverter typeConverter;

	public ColumnNameRowMapper(TypeHandlerRegistry typeHandlerRegistry, String columnName, JdbcType jdbcType,
			Class<T> requiredType, TypeConverter typeConverter) {
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.columnName = columnName;
		this.jdbcType = jdbcType;
		if (requiredType == null) {
			throw new IllegalArgumentException("The requiredType must not be null.");
		}
		this.requiredType = requiredType;
		this.typeConverter = typeConverter;
	}

	public T mapRow(long rowIndex, ResultSet rs) throws SQLException {
		Object result = getColumnValue(rs, jdbcType);
		Object converted = typeConverter != null ? typeConverter.convert(result, requiredType, null) : result;
		return requiredType.cast(converted);
	}

	protected Object getColumnValue(ResultSet rs, JdbcType jdbcType) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(null, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnName);
	}

}
