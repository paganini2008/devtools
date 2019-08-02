package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * StringTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class StringTypeHandler extends BaseTypeHandler {

	public StringTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(parameterIndex, getJavaType().cast(parameter));
	}

	protected String getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	protected String getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	protected String getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getString(columnIndex);
	}

	public Class<String> getJavaType() {
		return String.class;
	}

}
