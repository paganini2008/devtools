package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * SqlDateTypeHandler
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SqlDateTypeHandler extends BasicTypeHandler {

	public SqlDateTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setDate(parameterIndex, getJavaType().cast(parameter));
	}

	protected Date getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	protected Date getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	protected Date getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getDate(columnIndex);
	}

	public Class<Date> getJavaType() {
		return Date.class;
	}

}
