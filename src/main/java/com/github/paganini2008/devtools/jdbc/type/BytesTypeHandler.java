package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * BytesTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BytesTypeHandler extends BaseTypeHandler {

	public BytesTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setBytes(parameterIndex, getJavaType().cast(parameter));
	}

	protected byte[] getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getBytes(columnName);
	}

	protected byte[] getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBytes(columnIndex);
	}

	public byte[] getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getBytes(columnIndex);
	}

	public Class<byte[]> getJavaType() {
		return byte[].class;
	}

}
