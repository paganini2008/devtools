package com.github.paganini2008.devtools.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;

public interface SqlParameter {

	void setValue(PreparedStatement ps, int parameterIndex, TypeHandlerRegistry typeHandlerRegistry, TypeConverter typeConverter)
			throws SQLException;

}
