package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * ByteTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ByteTypeHandler extends BaseTypeHandler {

	public ByteTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setByte(parameterIndex, getJavaType().cast(parameter));
	}

	protected Byte getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	protected Byte getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	public Class<Byte> getJavaType() {
		return Byte.class;
	}

	public Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getByte(columnIndex);
	}

}
