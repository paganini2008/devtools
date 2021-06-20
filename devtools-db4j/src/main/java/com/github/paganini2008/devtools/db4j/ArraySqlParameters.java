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

import java.util.List;

/**
 * 
 * ArraySqlParameters
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ArraySqlParameters extends AbstractSqlParameter implements SqlParameters {

	private final List<Object[]> argsList;

	public ArraySqlParameters(List<Object[]> argsList) {
		this.argsList = argsList;
	}

	@Override
	public int getSize() {
		return argsList.size();
	}

	@Override
	public boolean hasValue(int index, String name) {
		try {
			return Integer.parseInt(name) < argsList.get(index).length;
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Override
	public Object getValue(int index, String name) {
		return argsList.get(index)[Integer.parseInt(name)];
	}

}
