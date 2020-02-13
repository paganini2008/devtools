package com.github.paganini2008.devtools.jdbc;

import java.util.List;

/**
 * 
 * PageResponse
 * 
 * @author Fred Feng
 * @created 2019-04
 * @revised 2019-04
 */
public interface PageResponse<T> extends Iterable<PageResponse<T>> {

	boolean isEmpty();

	boolean isLastPage();

	boolean isFirstPage();

	boolean hasNextPage();

	boolean hasPreviousPage();

	int getTotalPages();

	int getTotalRecords();

	int getOffset();

	int getPageSize();

	List<T> getContent();

	PageResponse<T> setPage(int pageNumber);

	PageResponse<T> lastPage();

	PageResponse<T> firstPage();

	PageResponse<T> nextPage();

	PageResponse<T> previousPage();

}
