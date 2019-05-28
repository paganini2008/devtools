package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * Handle the sqlParameters and sqlDataTypes
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface TypeHandler {

	void setValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType, TypeConverter typeConverter)
			throws SQLException;

	Object getValue(ResultSet rs, String columnName) throws SQLException;

	Object getValue(ResultSet rs, int columnIndex) throws SQLException;

	Object getValue(CallableStatement cs, int columnIndex) throws SQLException;
}
