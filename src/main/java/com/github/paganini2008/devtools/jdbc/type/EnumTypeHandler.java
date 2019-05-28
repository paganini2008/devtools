package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * EnumTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler {

	public EnumTypeHandler(Class<E> type) {
		super();
		this.type = type;
	}

	private final Class<E> type;

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(parameterIndex, getJavaType().cast(parameter).name());
	}

	protected Enum<E> getNullableValue(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return s == null ? null : Enum.valueOf(type, s);
	}

	protected Enum<E> getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);
		return s == null ? null : Enum.valueOf(type, s);
	}

	public Class<E> getJavaType() {
		return type;
	}

	protected Enum<E> getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);
		return s == null ? null : Enum.valueOf(type, s);
	}

}
