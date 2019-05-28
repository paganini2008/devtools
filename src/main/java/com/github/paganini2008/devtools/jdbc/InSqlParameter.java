package com.github.paganini2008.devtools.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.type.TypeHandler;

public class InSqlParameter implements SqlParameter {

	private final Object parameter;
	private final JdbcType jdbcType;

	public InSqlParameter(Object parameter, JdbcType jdbcType) {
		this.parameter = parameter;
		this.jdbcType = jdbcType;
	}

	public void setValue(PreparedStatement ps, int parameterIndex, TypeHandlerRegistry typeHandlerRegistry, TypeConverter typeConverter)
			throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(parameter != null ? parameter.getClass() : null, jdbcType);
		typeHandler.setValue(ps, parameterIndex, typeHandler, jdbcType, typeConverter);
	}

}
