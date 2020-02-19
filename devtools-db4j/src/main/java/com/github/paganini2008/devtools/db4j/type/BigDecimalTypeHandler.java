package com.github.paganini2008.devtools.db4j.type;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * BigDecimalTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigDecimalTypeHandler extends BasicTypeHandler {

	public BigDecimalTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setBigDecimal(parameterIndex, getJavaType().cast(parameter));
	}

	protected BigDecimal getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	protected BigDecimal getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	public BigDecimal getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getBigDecimal(columnIndex);
	}

	public Class<BigDecimal> getJavaType() {
		return BigDecimal.class;
	}

}
