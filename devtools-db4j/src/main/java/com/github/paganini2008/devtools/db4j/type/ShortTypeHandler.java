package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * ShortTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ShortTypeHandler extends BasicTypeHandler {

	public ShortTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setShort(parameterIndex, getJavaType().cast(parameter));
	}

	protected Short getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	protected Short getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	protected Short getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getShort(columnIndex);
	}

	public Class<Short> getJavaType() {
		return Short.class;
	}

}
