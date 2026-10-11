/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
 * Listable lists records in a range.
 * 
 * @author Fred Feng
 * @date 2021-05-12
 *
 * @since 2.0.1
 */
public interface Listable<T> {

	/**
	 * Lists all records.
	 */
	default List<T> list() {
		return list(-1);
	}

	/**
	 * Lists at most {@code maxResults} records from the beginning.
	 */
	default List<T> list(int maxResults) {
		return list(maxResults, 0);
	}

	/**
	 * Returns the first record, or null if there is none.
	 */
	default T first() {
		return ListUtils.getFirst(list(1));
	}

	/**
	 * Lists records in a range.
	 * 
	 * @param maxResults the max count of records, non-positive means no limit
	 * @param firstResult the 0-based offset of the first record
	 */
	List<T> list(int maxResults, int firstResult);

}
