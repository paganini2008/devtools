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
package com.github.paganini2008.devtools.db4j.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * BasicTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class BasicTypeHandler implements TypeHandler {

	protected BasicTypeHandler() {
	}

	public void setValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			setNonNullValue(ps, parameterIndex, ConvertUtils.convertValue(parameter, getJavaType()), jdbcType);
		} else {
			setNullValue(ps, parameterIndex, parameter, jdbcType);
		}
	}

	protected void setNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		if (jdbcType != JdbcType.OTHER || jdbcType != JdbcType.OBJECT) {
			ps.setNull(parameterIndex, jdbcType.getTypeCode());
		} else {
			ps.setObject(parameterIndex, null);
		}
	}

	public Object getValue(ResultSet rs, String columnName) throws SQLException {
		Object result = getNullableValue(rs, columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
		Object result = getNullableValue(rs, columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public Object getValue(CallableStatement cs, int columnIndex) throws SQLException {
		Object result = getNullableValue(cs, columnIndex);
		if (cs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	protected abstract void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType)
			throws SQLException;

	protected abstract Object getNullableValue(ResultSet rs, String columnName) throws SQLException;

	protected abstract Object getNullableValue(ResultSet rs, int columnIndex) throws SQLException;

	protected abstract Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException;

	public abstract Class<?> getJavaType();

}
