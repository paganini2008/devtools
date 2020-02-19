package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

public class LongTypeHandler extends BasicTypeHandler {

	public LongTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setLong(parameterIndex, getJavaType().cast(parameter));
	}

	protected Long getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	protected Long getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getLong(columnIndex);
	}

	public Class<Long> getJavaType() {
		return Long.class;
	}

}
