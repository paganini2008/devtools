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

import java.util.List;

import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * BeanPropertySqlParameters
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BeanPropertySqlParameters extends AbstractSqlParameter implements SqlParameters {

	public BeanPropertySqlParameters(List<?> objectList) {
		this.objectList = objectList;
	}

	private final List<?> objectList;

	public int getSize() {
		return objectList.size();
	}

	public boolean hasValue(int index, String paramName) {
		Object o = objectList.get(index);
		return PropertyUtils.hasProperty(o.getClass(), paramName);
	}

	public Object getValue(int index, String paramName) {
		Object o = objectList.get(index);
		return PropertyUtils.getProperty(o, paramName);
	}

}
