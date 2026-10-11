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

import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * PageableQuery is a query which can be listed in ranges, iterated by cursors
 * and paged.
 *
 * @author Fred Feng
 * @date 2019-08-01
 * @since 2.0.1
 */
public interface PageableQuery<T> extends ResultSetSlice<T> {

	/**
	 * Lists records in a range by reading a cursor to its end.
	 */
	default List<T> list(int maxResults, int firstResult) {
		List<T> results = new ArrayList<T>();
		Cursor<T> cursor = cursor(maxResults, firstResult);
		for (T t : CollectionUtils.forEach(cursor)) {
			results.add(t);
		}
		return results;
	}

	/**
	 * Opens a cursor over a range of records.
	 * 
	 * @throws PageableException if the query fails
	 */
	Cursor<T> cursor(int maxResults, int firstResult);

	/**
	 * Iterates the pages starting from the given page.
	 */
	default Iterable<PageResponse<T>> forEach(int page, int size) {
		return list(PageRequest.of(page, size));
	}

}
