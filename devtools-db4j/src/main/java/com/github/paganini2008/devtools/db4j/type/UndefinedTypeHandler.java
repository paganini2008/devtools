package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;

/**
 * UndefinedTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class UndefinedTypeHandler implements TypeHandler {

	public UndefinedTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	private final TypeHandlerRegistry typeHandlerRegistry;

	public void setValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(parameter != null ? parameter.getClass() : null, jdbcType);
		try {
			typeHandler.setValue(ps, parameterIndex, parameter, jdbcType);
		} catch (SQLException e) {
			if (!(typeHandler instanceof ObjectTypeHandler)) {
				TypeHandlerRegistryImpl.getDefault().setValue(ps, parameterIndex, parameter, jdbcType);
			} else {
				throw e;
			}
		}
	}

	public Object getValue(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String columnLabel;
		for (int i = 0; i < columnCount; i++) {
			columnLabel = rsmd.getColumnLabel(i);
			if (columnLabel.equals(columnName)) {
				return getValue(rs, i);
			}
		}
		throw new IllegalStateException("Unknown column: " + columnName);
	}

	public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(null, JdbcType.find(rs.getMetaData().getColumnType(columnIndex)));
		try {
			return typeHandler.getValue(rs, columnIndex);
		} catch (SQLException e) {
			if (!(typeHandler instanceof ObjectTypeHandler)) {
				return TypeHandlerRegistryImpl.getDefault().getValue(rs, columnIndex);
			} else {
				throw e;
			}
		}
	}

	public Object getValue(CallableStatement cs, int columnIndex) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(null, JdbcType.find(cs.getMetaData().getColumnType(columnIndex)));
		try {
			return typeHandler.getValue(cs, columnIndex);
		} catch (SQLException e) {
			if (!(typeHandler instanceof ObjectTypeHandler)) {
				return TypeHandlerRegistryImpl.getDefault().getValue(cs, columnIndex);
			} else {
				throw e;
			}
		}
	}

}
