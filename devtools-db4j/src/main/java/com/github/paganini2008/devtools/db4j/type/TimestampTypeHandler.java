package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * TimestampTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class TimestampTypeHandler extends BasicTypeHandler {

	public TimestampTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(parameterIndex, getJavaType().cast(parameter));
	}

	protected Timestamp getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	protected Timestamp getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTimestamp(columnIndex);
	}

	public Class<Timestamp> getJavaType() {
		return Timestamp.class;
	}

}
