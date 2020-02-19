package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * DoubleTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleTypeHandler extends BasicTypeHandler {

	public DoubleTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setDouble(parameterIndex, getJavaType().cast(parameter));
	}

	protected Double getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	protected Double getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	public Double getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getDouble(columnIndex);
	}

	public Class<Double> getJavaType() {
		return Double.class;
	}

}
