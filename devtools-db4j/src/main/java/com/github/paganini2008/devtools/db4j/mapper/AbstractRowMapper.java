package com.github.paganini2008.devtools.db4j.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.db4j.Db4jUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * 
 * AbstractRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T> {

	public T mapRow(int rowIndex, ResultSet rs, TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		T object = createObject(columnCount);
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnName = getColumnName(rsmd, columnIndex);
			String columnDisplayName = getColumnDisplayName(rsmd, columnIndex);
			Type javaType = getJavaType(rsmd, columnIndex);
			JdbcType jdbcType = getJdbcType(rsmd, columnIndex);
			Object columnValue = getColumnValue(rs, columnName, columnIndex, javaType, jdbcType, typeHandlerRegistry);
			setValue(object, columnIndex, columnName, columnDisplayName, jdbcType, columnValue);
		}
		return object;
	}

	private boolean useCamelCase = true;

	protected abstract T createObject(int columnCount);

	protected abstract void setValue(T object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue);

	public void setUseCamelCase(boolean useCamelCase) {
		this.useCamelCase = useCamelCase;
	}

	protected Type getJavaType(ResultSetMetaData rsmd, int columnIndex) {
		String className;
		try {
			className = rsmd.getColumnClassName(columnIndex);
		} catch (SQLException e) {
			return null;
		}
		return Db4jUtils.getClassNamesAndJavaTypes().get(className);
	}

	protected JdbcType getJdbcType(ResultSetMetaData rsmd, int columnIndex) {
		try {
			return JdbcType.find(rsmd.getColumnType(columnIndex));
		} catch (SQLException e) {
			return null;
		}
	}

	protected String getColumnDisplayName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
		String columnDisplayName = rsmd.getColumnLabel(columnIndex);
		return useCamelCase ? StringUtils.toCamelCase(columnDisplayName, "_") : columnDisplayName;
	}

	protected String getColumnName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
		return rsmd.getColumnName(columnIndex);
	}

	protected Object getColumnValue(ResultSet rs, String columnName, int columnIndex, Type javaType, JdbcType jdbcType,
			TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(javaType, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnIndex);
	}
}
