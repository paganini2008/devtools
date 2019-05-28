package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * BooleanTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BooleanTypeHandler extends BaseTypeHandler {

	public BooleanTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setBoolean(parameterIndex, getJavaType().cast(parameter));
	}

	protected Boolean getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	protected Boolean getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	public Boolean getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getBoolean(columnIndex);
	}

	public Class<Boolean> getJavaType() {
		return Boolean.class;
	}

}
