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

import java.util.Map;

/**
 * MapSqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MapSqlParameter extends AbstractSqlParameter implements SqlParameter {

	public MapSqlParameter(Map<String, ?> parameterMap) {
		this(parameterMap, null);
	}

	public MapSqlParameter(Map<String, ?> parameterMap, Map<String, JdbcType> jdbcTypeMap) {
		this.parameterMap = parameterMap;
		if (jdbcTypeMap != null) {
			for (Map.Entry<String, JdbcType> e : jdbcTypeMap.entrySet()) {
				addJdbcType(e.getKey(), e.getValue());
			}
		}
	}

	private final Map<String, ?> parameterMap;

	public boolean hasValue(String paramName) {
		return parameterMap.containsKey(paramName);
	}

	public Object getValue(String paramName) {
		return parameterMap.get(paramName);
	}

}
