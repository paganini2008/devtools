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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * TimestampTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class TimestampTypeHandler extends BasicTypeHandler {

	public TimestampTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(parameterIndex, getJavaType().cast(parameter));
	}

	protected Timestamp getNullableValue(ResultSet rs, String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	protected Timestamp getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	protected Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTimestamp(columnIndex);
	}

	public Class<Timestamp> getJavaType() {
		return Timestamp.class;
	}

}
