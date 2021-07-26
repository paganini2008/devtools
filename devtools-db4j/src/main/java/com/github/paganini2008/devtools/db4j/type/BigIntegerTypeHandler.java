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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * BigIntegerTypeHandler
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class BigIntegerTypeHandler extends BasicTypeHandler {

	public BigIntegerTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setBigDecimal(parameterIndex, new BigDecimal(getJavaType().cast(parameter)));
	}

	protected BigInteger getNullableValue(ResultSet rs, String columnName) throws SQLException {
		BigDecimal result = rs.getBigDecimal(columnName);
		return result != null ? result.toBigInteger() : null;
	}

	protected BigInteger getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		BigDecimal result = rs.getBigDecimal(columnIndex);
		return result != null ? result.toBigInteger() : null;
	}

	public BigInteger getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		BigDecimal result = cs.getBigDecimal(columnIndex);
		return result != null ? result.toBigInteger() : null;
	}

	public Class<BigInteger> getJavaType() {
		return BigInteger.class;
	}

}
