package com.github.paganini2008.devtools.jdbc.type;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * ArrayTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ArrayTypeHandler extends BaseTypeHandler {

	public ArrayTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setArray(parameterIndex, getJavaType().cast(parameter));
	}

	protected Object getNullableValue(ResultSet rs, String columnName) throws SQLException {
		Array array = rs.getArray(columnName);
		return array != null ? array.getArray() : null;
	}

	protected Object getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		Array array = rs.getArray(columnIndex);
		return array != null ? array.getArray() : null;
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		Array array = cs.getArray(columnIndex);
		return array != null ? array.getArray() : null;
	}

	public Class<Array> getJavaType() {
		return Array.class;
	}

}
