package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * CharacterTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharacterTypeHandler extends BaseTypeHandler {

	public CharacterTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(parameterIndex, String.valueOf(getJavaType().cast(parameter)));
	}

	protected Character getNullableValue(ResultSet rs, String columnName) throws SQLException {
		String columnValue = rs.getString(columnName);
		if (columnValue != null) {
			return columnValue.charAt(0);
		} else {
			return null;
		}
	}

	protected Character getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		String columnValue = rs.getString(columnIndex);
		if (columnValue != null) {
			return columnValue.charAt(0);
		} else {
			return null;
		}
	}

	public Character getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		String columnValue = cs.getString(columnIndex);
		if (columnValue != null) {
			return columnValue.charAt(0);
		} else {
			return null;
		}
	}

	public Class<Character> getJavaType() {
		return Character.class;
	}

}
