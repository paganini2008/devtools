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
package com.github.paganini2008.devtools.db4j;

import java.util.HashMap;
import java.util.Map;

/**
 * AbstractSqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractSqlParameter implements SqlType {

	private final Map<String, JdbcType> jdbcTypeMap = new HashMap<String, JdbcType>();

	public void addJdbcType(String paramName, JdbcType jdbcType) {
		if (jdbcType == null) {
			jdbcType = JdbcType.OTHER;
		}
		jdbcTypeMap.put(paramName, jdbcType);
	}

	public JdbcType getJdbcType(String paramName) {
		return jdbcTypeMap.containsKey(paramName) ? jdbcTypeMap.get(paramName) : JdbcType.OTHER;
	}

}
