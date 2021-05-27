package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * NullTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class NullTypeHandler extends BasicTypeHandler {

	public NullTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		throw new UnsupportedOperationException();
	}

	protected Object getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getObject(columnName);
	}

	protected Object getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}

	public Class<?> getJavaType() {
		throw new UnsupportedOperationException();
	}

}
