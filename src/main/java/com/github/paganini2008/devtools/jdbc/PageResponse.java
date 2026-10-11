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

/**
 * 
 * PageResponse is one page of results together with the paging information;
 * iterating it yields this page and all following pages.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 *
 * @since 2.0.1
 */
public interface PageResponse<T> extends Iterable<PageResponse<T>> {

	/**
	 * Returns true if there is no record at all.
	 */
	boolean isEmpty();

	boolean isLastPage();

	boolean isFirstPage();

	boolean hasNextPage();

	boolean hasPreviousPage();

	/**
	 * Returns the count of pages, 0 if there is no record.
	 */
	int getTotalPages();

	int getTotalRecords();

	int getOffset();

	int getPageSize();

	int getPageNumber();

	/**
	 * Loads the records of this page.
	 */
	List<T> getContent();

	/**
	 * Returns the given page.
	 */
	PageResponse<T> setPage(int pageNumber);

	/**
	 * Returns the last page, or this page if it is the last one.
	 */
	PageResponse<T> lastPage();

	/**
	 * Returns the first page, or this page if it is the first one.
	 */
	PageResponse<T> firstPage();

	/**
	 * Returns the next page, or this page if there is no next page.
	 */
	PageResponse<T> nextPage();

	/**
	 * Returns the previous page, or this page if there is no previous page.
	 */
	PageResponse<T> previousPage();

}
