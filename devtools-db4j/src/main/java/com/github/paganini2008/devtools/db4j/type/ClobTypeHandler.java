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
package com.github.paganini2008.devtools.db4j.type;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * ClobTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ClobTypeHandler extends BasicTypeHandler {

	public ClobTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		String content = getJavaType().cast(parameter);
		StringReader reader = new StringReader(content);
		ps.setCharacterStream(parameterIndex, reader, content.length());
	}

	protected String getNullableValue(ResultSet rs, String columnName) throws SQLException {
		Clob clob = rs.getClob(columnName);
		if (clob != null) {
			int size = (int) clob.length();
			return clob.getSubString(1, size);
		}
		return null;
	}

	protected String getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		Clob clob = rs.getClob(columnIndex);
		if (clob != null) {
			int size = (int) clob.length();
			return clob.getSubString(1, size);
		}
		return null;
	}

	public Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		Clob clob = cs.getClob(columnIndex);
		if (clob != null) {
			int size = (int) clob.length();
			return clob.getSubString(1, size);
		}
		return null;
	}

	public Class<String> getJavaType() {
		return String.class;
	}
}
