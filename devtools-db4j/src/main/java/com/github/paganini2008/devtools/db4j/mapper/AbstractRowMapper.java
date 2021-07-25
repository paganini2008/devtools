/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.db4j.Db4jUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * 
 * AbstractRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T> {

	public T mapRow(int rowIndex, ResultSet rs, TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		T object = createObject(columnCount);
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnName = getColumnName(rsmd, columnIndex);
			String columnDisplayName = getColumnDisplayName(rsmd, columnIndex);
			Type javaType = getJavaType(rsmd, columnIndex);
			JdbcType jdbcType = getJdbcType(rsmd, columnIndex);
			Object columnValue = getColumnValue(rs, columnName, columnIndex, javaType, jdbcType, typeHandlerRegistry);
			setValue(object, columnIndex, columnName, columnDisplayName, jdbcType, columnValue);
		}
		return object;
	}

	protected abstract T createObject(int columnCount);

	protected abstract void setValue(T object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue);

	protected Type getJavaType(ResultSetMetaData rsmd, int columnIndex) {
		String className;
		try {
			className = rsmd.getColumnClassName(columnIndex);
		} catch (SQLException e) {
			return null;
		}
		return Db4jUtils.getClassNamesAndJavaTypes().get(className);
	}

	protected JdbcType getJdbcType(ResultSetMetaData rsmd, int columnIndex) {
		try {
			return JdbcType.find(rsmd.getColumnType(columnIndex));
		} catch (SQLException e) {
			return null;
		}
	}

	protected String getColumnDisplayName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
		return rsmd.getColumnLabel(columnIndex);
	}

	protected String getColumnName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
		return rsmd.getColumnName(columnIndex);
	}

	protected Object getColumnValue(ResultSet rs, String columnName, int columnIndex, Type javaType, JdbcType jdbcType,
			TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(javaType, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnIndex);
	}
}
