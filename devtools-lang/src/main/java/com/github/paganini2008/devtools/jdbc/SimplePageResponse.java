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
package com.github.paganini2008.devtools.jdbc;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * SimplePageResponse
 *
 * @author Fred Feng
 * @version 1.0
 */
public class SimplePageResponse<T> implements PageResponse<T>, Serializable {

	private static final long serialVersionUID = 2344670418378384182L;
	private final int pageNumber;
	private final int totalPages;
	private final int totalRecords;
	private final PageRequest pageRequest;
	private final ResultSetSlice<T> resultSetSlice;

	public SimplePageResponse(PageRequest pageRequest, ResultSetSlice<T> resultSetSlice) {
		this(pageRequest, resultSetSlice, () -> {
			return resultSetSlice.rowCount();
		});
	}

	public SimplePageResponse(PageRequest pageRequest, ResultSetSlice<T> resultSetSlice, Countable countable) {
		this.pageNumber = pageRequest.getPageNumber();
		this.totalRecords = countable.rowCount();
		this.totalPages = (totalRecords + pageRequest.getPageSize() - 1) / pageRequest.getPageSize();
		this.pageRequest = pageRequest;
		this.resultSetSlice = resultSetSlice;
	}

	public boolean isEmpty() {
		return totalRecords == 0;
	}

	public boolean isLastPage() {
		return pageNumber == getTotalPages();
	}

	public boolean isFirstPage() {
		return pageNumber == 1;
	}

	public boolean hasNextPage() {
		return pageNumber < getTotalPages();
	}

	public boolean hasPreviousPage() {
		return pageNumber > 1;
	}

	public Iterator<PageResponse<T>> iterator() {
		return new PageIterator<T>(this);
	}

	/**
	 * 
	 * PageIterator
	 * 
	 * @author Fred Feng
	 *
	 * @version 1.0
	 */
	static class PageIterator<T> implements Iterator<PageResponse<T>> {

		private final PageResponse<T> pageResponse;
		private PageResponse<T> current;

		PageIterator(PageResponse<T> pageResponse) {
			this.pageResponse = pageResponse;
		}

		public boolean hasNext() {
			return current == null || current.hasNextPage();
		}

		public PageResponse<T> next() {
			if (current == null) {
				current = pageResponse;
			} else {
				current = current.nextPage();
			}
			return current;
		}
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public int getOffset() {
		return pageRequest.getOffset();
	}

	public int getPageSize() {
		return pageRequest.getPageSize();
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public PageResponse<T> setPage(int pageNumber) {
		return new SimplePageResponse<T>(pageRequest.set(pageNumber), resultSetSlice);
	}

	public PageResponse<T> lastPage() {
		int lastPage = getTotalPages();
		return isLastPage() ? this : setPage(lastPage);
	}

	public PageResponse<T> firstPage() {
		return isFirstPage() ? this : new SimplePageResponse<T>(pageRequest.first(), resultSetSlice);
	}

	public PageResponse<T> nextPage() {
		return hasNextPage() ? new SimplePageResponse<T>(pageRequest.next(), resultSetSlice) : this;
	}

	public PageResponse<T> previousPage() {
		return hasPreviousPage() ? new SimplePageResponse<T>(pageRequest.previous(), resultSetSlice) : this;
	}

	public List<T> getContent() {
		return resultSetSlice.list(pageRequest.getPageSize(), pageRequest.getOffset());
	}

}
