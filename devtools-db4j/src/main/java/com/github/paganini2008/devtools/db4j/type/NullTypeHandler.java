/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * NullTypeHandler
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class NullTypeHandler extends BasicTypeHandler {

	public NullTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		throw new UnsupportedOperationException();
	}

	protected Object getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getObject(columnName);
	}

	protected Object getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}

	public Class<?> getJavaType() {
		throw new UnsupportedOperationException();
	}

}
