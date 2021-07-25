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
package com.github.paganini2008.devtools.jdbc;

import java.util.List;

import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * 
 * Listable
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface Listable<T> {

	default List<T> list() {
		return list(-1);
	}

	default List<T> list(int maxResults) {
		return list(maxResults, 0);
	}

	default T first() {
		return ListUtils.getFirst(list(1));
	}

	List<T> list(int maxResults, int firstResult);

}
