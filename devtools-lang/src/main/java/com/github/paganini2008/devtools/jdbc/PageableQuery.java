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

import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * PageableQuery
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface PageableQuery<T> extends ResultSetSlice<T> {

	default List<T> list(int maxResults, int firstResult) {
		List<T> results = new ArrayList<T>();
		Cursor<T> cursor = cursor(maxResults, firstResult);
		for (T t : CollectionUtils.forEach(cursor)) {
			results.add(t);
		}
		return results;
	}

	Cursor<T> cursor(int maxResults, int firstResult);

	default Iterable<PageResponse<T>> forEach(int page, int size) {
		return list(PageRequest.of(page, size));
	}

}
