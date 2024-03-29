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
package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * RowMapperResultSetExtractor
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private final RowMapper<T> rowMapper;
	private final TypeHandlerRegistry typeHandlerRegistry;

	public RowMapperResultSetExtractor(RowMapper<T> rowMapper, TypeHandlerRegistry typeHandlerRegistry) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<T>();
		int rownum = 1;
		while (rs.next()) {
			results.add(this.rowMapper.mapRow(rownum++, rs, typeHandlerRegistry));
		}
		return results;
	}

}
