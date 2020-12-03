package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * SqlTimeTypeHandler
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SqlTimeTypeHandler extends BasicTypeHandler {

	public SqlTimeTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setTime(parameterIndex, getJavaType().cast(parameter));
	}

	protected Time getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	protected Time getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	protected Time getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTime(columnIndex);
	}

	public Class<Time> getJavaType() {
		return Time.class;
	}
}
