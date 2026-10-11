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
 * PageRequest describes a page to fetch: a 1-based page number and a page
 * size.
 *
 * @author Fred Feng
 * @date 2019-07-11
 *
 * @since 2.0.1
 */
public interface PageRequest {

	/**
	 * Returns the 1-based page number.
	 */
	int getPageNumber();

	int getPageSize();

	/**
	 * Returns the 0-based offset of the first record of this page.
	 */
	int getOffset();

	/**
	 * Returns the request of the next page.
	 */
	PageRequest next();

	/**
	 * Returns the request of the previous page, or this request on the first page.
	 */
	PageRequest previous();

	/**
	 * Returns the request of the first page.
	 */
	PageRequest first();

	/**
	 * Returns the request of the given page with the same page size.
	 * 
	 * @throws IllegalArgumentException if the page is less than 1
	 */
	PageRequest set(int page);

	/**
	 * Creates the request of the first page.
	 * 
	 * @throws IllegalArgumentException if the size is less than 1
	 */
	static PageRequest of(int size) {
		return of(1, size);
	}

	/**
	 * Creates a page request.
	 * 
	 * @throws IllegalArgumentException if the page or the size is less than 1
	 */
	static PageRequest of(int page, int size) {
		return new SimplePageRequest(page, size);
	}
}
