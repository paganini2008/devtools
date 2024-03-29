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
package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * Cursor
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface Cursor<T> extends Iterator<T> {

	default List<T> list() {
		List<T> results = new ArrayList<T>();
		for (T t : CollectionUtils.forEach(this)) {
			results.add(t);
		}
		return results;
	}
	
	int getRownum();
	
	void mark(int rownum);

	boolean isOpened();

	void close();

}
