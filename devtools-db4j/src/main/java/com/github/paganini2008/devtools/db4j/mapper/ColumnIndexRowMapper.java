package com.github.paganini2008.devtools.db4j.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * 
 * ColumnIndexRowMapper
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class ColumnIndexRowMapper<T> implements RowMapper<T> {

	public ColumnIndexRowMapper(TypeHandlerRegistry typeHandlerRegistry, Class<T> requiredType) {
		this(typeHandlerRegistry, 1, requiredType);
	}

	public ColumnIndexRowMapper(TypeHandlerRegistry typeHandlerRegistry, int columnIndex, Class<T> requiredType) {
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.columnIndex = columnIndex;
		if (requiredType == null) {
			throw new IllegalArgumentException("The requiredType must not be null.");
		}
		this.requiredType = requiredType;
	}

	private final TypeHandlerRegistry typeHandlerRegistry;
	private final Class<T> requiredType;
	private final int columnIndex;

	public T mapRow(int rowIndex, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		String columnName = getColumnName(rsmd);
		JdbcType jdbcType = getJdbcType(rsmd);
		Object result = getColumnValue(rs, columnName, columnIndex, jdbcType);
		return ConvertUtils.convertValue(result, requiredType);
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
