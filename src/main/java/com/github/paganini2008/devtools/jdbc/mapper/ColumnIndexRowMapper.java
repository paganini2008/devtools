package com.github.paganini2008.devtools.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.jdbc.type.TypeHandler;

public class ColumnIndexRowMapper<T> implements RowMapper<T> {

	public ColumnIndexRowMapper(TypeHandlerRegistry typeHandlerRegistry, Class<T> requiredType, TypeConverter typeConverter) {
		this(typeHandlerRegistry, 1, requiredType, typeConverter);
	}

	public ColumnIndexRowMapper(TypeHandlerRegistry typeHandlerRegistry, int columnIndex, Class<T> requiredType,
			TypeConverter typeConverter) {
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.columnIndex = columnIndex;
		if (requiredType == null) {
			throw new IllegalArgumentException("The requiredType must not be null.");
		}
		this.requiredType = requiredType;
		this.typeConverter = typeConverter;
	}

	private final TypeHandlerRegistry typeHandlerRegistry;
	private final Class<T> requiredType;
	private final int columnIndex;
	private final TypeConverter typeConverter;

	public T mapRow(long rowIndex, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		String columnName = getColumnName(rsmd);
		JdbcType jdbcType = getJdbcType(rsmd);
		Object result = getColumnValue(rs, columnName, columnIndex, jdbcType);
		Object converted = typeConverter != null ? typeConverter.convert(result, requiredType, null) : result;
		return requiredType.cast(converted);
	}

	protected JdbcType getJdbcType(ResultSetMetaData rsmd) {
		try {
			return JdbcType.find(rsmd.getColumnType(columnIndex));
		} catch (SQLException e) {
			return null;
		}
	}

	protected final String getColumnName(ResultSetMetaData rsmd) throws SQLException {
		return rsmd.getColumnName(columnIndex);
	}

	protected Object getColumnValue(ResultSet rs, String columnName, int columnIndex, JdbcType jdbcType) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(null, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnIndex);
	}

}
