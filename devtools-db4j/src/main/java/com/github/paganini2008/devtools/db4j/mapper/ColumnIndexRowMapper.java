/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.db4j.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.db4j.Db4jUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * 
 * ColumnIndexRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ColumnIndexRowMapper<T> implements RowMapper<T> {

	public ColumnIndexRowMapper(Class<T> requiredType) {
		this(1, requiredType);
	}

	public ColumnIndexRowMapper(int columnIndex, Class<T> requiredType) {
		this.columnIndex = columnIndex;
		if (requiredType == null) {
			throw new IllegalArgumentException("The requiredType must not be null.");
		}
		this.requiredType = requiredType;
	}

	private final Class<T> requiredType;
	private final int columnIndex;

	public T mapRow(int rowIndex, ResultSet rs, TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		String columnName = getColumnName(rsmd);
		Type javaType = getJavaType(rsmd);
		JdbcType jdbcType = getJdbcType(rsmd);
		Object result = getColumnValue(rs, columnName, columnIndex, javaType, jdbcType, typeHandlerRegistry);
		return ConvertUtils.convertValue(result, requiredType);
	}

	protected JdbcType getJdbcType(ResultSetMetaData rsmd) {
		try {
			return JdbcType.find(rsmd.getColumnType(columnIndex));
		} catch (SQLException e) {
			return null;
		}
	}

	protected Type getJavaType(ResultSetMetaData rsmd) {
		String className;
		try {
			className = rsmd.getColumnClassName(columnIndex);
		} catch (SQLException e) {
			return null;
		}
		return Db4jUtils.getClassNamesAndJavaTypes().get(className);
	}

	protected final String getColumnName(ResultSetMetaData rsmd) throws SQLException {
		return rsmd.getColumnName(columnIndex);
	}

	protected Object getColumnValue(ResultSet rs, String columnName, int columnIndex, Type javaType, JdbcType jdbcType,
			TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(javaType, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnIndex);
	}

}
