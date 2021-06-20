/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * FirstRowResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FirstRowResultSetExtractor<T> implements ResultSetExtractor<T> {

	private final RowMapper<T> rowMapper;
	private final TypeHandlerRegistry typeHandlerRegistry;

	public FirstRowResultSetExtractor(RowMapper<T> rowMapper, TypeHandlerRegistry typeHandlerRegistry) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public T extractData(ResultSet rs) throws SQLException {
		return (rs != null) && (rs.next()) ? rowMapper.mapRow(1, rs, typeHandlerRegistry) : null;
	}

}
