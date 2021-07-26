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

import java.util.Map;

import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * BeanPropertySqlParameter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class BeanPropertySqlParameter extends AbstractSqlParameter implements SqlParameter {

	public BeanPropertySqlParameter(Object object) {
		this(object, null);
	}

	public BeanPropertySqlParameter(Object object, Map<String, JdbcType> jdbcTypeMap) {
		this.object = object;
		if (jdbcTypeMap != null) {
			for (Map.Entry<String, JdbcType> e : jdbcTypeMap.entrySet()) {
				addJdbcType(e.getKey(), e.getValue());
			}
		}
	}

	private final Object object;

	public boolean hasValue(String paramName) {
		return PropertyUtils.hasProperty(object.getClass(), paramName);
	}

	public Object getValue(String paramName) {
		return PropertyUtils.getProperty(object, paramName);
	}

}
