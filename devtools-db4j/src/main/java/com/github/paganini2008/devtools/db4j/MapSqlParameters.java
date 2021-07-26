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
package com.github.paganini2008.devtools.db4j;

import java.util.List;
import java.util.Map;

/**
 * MapSqlParameters
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class MapSqlParameters extends AbstractSqlParameter implements SqlParameters {

	private final List<Map<String, Object>> parameterList;

	public MapSqlParameters(List<Map<String, Object>> parameterList) {
		this(parameterList, null);
	}

	public MapSqlParameters(List<Map<String, Object>> parameterList, Map<String, JdbcType> jdbcTypeMap) {
		this.parameterList = parameterList;
		if (jdbcTypeMap != null) {
			for (Map.Entry<String, JdbcType> e : jdbcTypeMap.entrySet()) {
				addJdbcType(e.getKey(), e.getValue());
			}
		}
	}

	public boolean hasValue(int index, String paramName) {
		return parameterList.get(index).containsKey(paramName);
	}

	public Object getValue(int index, String paramName) {
		return parameterList.get(index).get(paramName);
	}

	public int getSize() {
		return parameterList.size();
	}

}
