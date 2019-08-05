package com.github.paganini2008.devtools.jdbc;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.type.TypeHandler;

/**
 * InOutSqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class InOutSqlParameter implements SqlParameter {

	private final Object parameter;
	private final JdbcType jdbcType;

	public InOutSqlParameter(Object parameter, JdbcType jdbcType) {
		this.parameter = parameter;
		this.jdbcType = jdbcType;
	}

	public void setValue(PreparedStatement ps, int parameterIndex, TypeHandlerRegistry typeHandlerRegistry, TypeConverter typeConverter)
			throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(parameter != null ? parameter.getClass() : null, jdbcType);
		typeHandler.setValue(ps, parameterIndex, typeHandler, jdbcType, typeConverter);
		((CallableStatement) ps).registerOutParameter(parameterIndex, jdbcType.getTypeCode());
	}

}
