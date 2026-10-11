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

/**
 * 
 * ResultSetSlice is a countable, listable result which can be paged.
 *
 * @author Fred Feng
 * @date 2019-07-11
 * @since 2.0.1
 */
public interface ResultSetSlice<T> extends Listable<T>, Countable {

	/**
	 * Returns the requested page, counting the records by {@link #rowCount()}.
	 */
	default PageResponse<T> list(PageRequest pageRequest) {
		return new SimplePageResponse<T>(pageRequest, this);
	}

	/**
	 * Returns the requested page, taking {@code maxResults} as the total count
	 * of records.
	 */
	default PageResponse<T> list(PageRequest pageRequest, int maxResults) {
		return new SimplePageResponse<T>(pageRequest, this, () -> maxResults);
	}

	/**
	 * Returns the requested page, counting the records by the given countable.
	 */
	default PageResponse<T> list(PageRequest pageRequest, Countable countable) {
		return new SimplePageResponse<T>(pageRequest, this, countable);
	}
}
