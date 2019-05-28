package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * IntegerTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IntegerTypeHandler extends BaseTypeHandler {

	public IntegerTypeHandler() {
		super();
	}

	public void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(parameterIndex, getJavaType().cast(parameter));
	}

	public Integer getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	public Integer getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	protected Integer getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getInt(columnIndex);
	}

	public Class<Integer> getJavaType() {
		return Integer.class;
	}

}
