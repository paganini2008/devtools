package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * 
 * TypeHandler
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface TypeHandler {

	void setValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException;

	Object getValue(ResultSet rs, String columnName) throws SQLException;

	Object getValue(ResultSet rs, int columnIndex) throws SQLException;

	Object getValue(CallableStatement cs, int columnIndex) throws SQLException;
}
