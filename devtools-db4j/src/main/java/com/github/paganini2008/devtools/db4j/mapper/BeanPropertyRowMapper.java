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
package com.github.paganini2008.devtools.db4j.mapper;

import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * 
 * BeanPropertyRowMapper
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class BeanPropertyRowMapper<T> extends AbstractRowMapper<T> {

	private final Class<T> elementType;

	public BeanPropertyRowMapper(Class<T> elementType) {
		this.elementType = elementType;
	}

	protected T createObject(int columnCount) {
		try {
			return ConstructorUtils.invokeConstructor(elementType, (Object[]) null);
		} catch (Exception e) {
			throw new RowMapperException(e);
		}
	}

	protected void setValue(T object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType, Object columnValue) {
		try {
			PropertyUtils.setProperty(object, columnDisplayName, columnValue);
		} catch (RuntimeException ignored) {
		}
	}

}
