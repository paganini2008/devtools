package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * FloatTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FloatTypeHandler extends BaseTypeHandler {

	public FloatTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setFloat(parameterIndex, getJavaType().cast(parameter));
	}

	protected Float getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	protected Float getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	protected Float getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getFloat(columnIndex);
	}

	public Class<Float> getJavaType() {
		return Float.class;
	}

}