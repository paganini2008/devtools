package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * ObjectTypeHandler
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ObjectTypeHandler extends BasicTypeHandler {

	public ObjectTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		if (jdbcType == null || jdbcType == JdbcType.OTHER) {
			ps.setObject(parameterIndex, parameter);
		} else {
			ps.setObject(parameterIndex, parameter, jdbcType.getTypeCode());
		}

	}

	protected Object getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getObject(columnName);
	}

	protected Object getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	public Class<?> getJavaType() {
		return Object.class;
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}

}
