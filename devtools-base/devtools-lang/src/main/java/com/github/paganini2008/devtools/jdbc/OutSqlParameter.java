package com.github.paganini2008.devtools.jdbc;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;

public class OutSqlParameter implements SqlParameter {

	private final JdbcType jdbcType;

	public OutSqlParameter(JdbcType jdbcType) {
		this.jdbcType = jdbcType;
	}

	public void setValue(PreparedStatement ps, int parameterIndex, TypeHandlerRegistry typeHandlerRegistry, TypeConverter typeConverter)
			throws SQLException {
		((CallableStatement) ps).registerOutParameter(parameterIndex, jdbcType.getTypeCode());
	}

}
