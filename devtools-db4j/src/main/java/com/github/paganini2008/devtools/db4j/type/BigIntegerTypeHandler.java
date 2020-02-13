package com.github.paganini2008.devtools.db4j.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * BigIntegerTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigIntegerTypeHandler extends BaseTypeHandler {

	public BigIntegerTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setBigDecimal(parameterIndex, new BigDecimal(getJavaType().cast(parameter)));
	}

	protected BigInteger getNullableValue(ResultSet rs, String columnName) throws SQLException {
		BigDecimal result = rs.getBigDecimal(columnName);
		return result != null ? result.toBigInteger() : null;
	}

	protected BigInteger getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		BigDecimal result = rs.getBigDecimal(columnIndex);
		return result != null ? result.toBigInteger() : null;
	}

	public BigInteger getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		BigDecimal result = cs.getBigDecimal(columnIndex);
		return result != null ? result.toBigInteger() : null;
	}

	public Class<BigInteger> getJavaType() {
		return BigInteger.class;
	}

}
