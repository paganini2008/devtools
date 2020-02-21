package com.github.paganini2008.devtools.jdbc;

import java.util.List;

/**
 * 
 * PageableQuery
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface PageableQuery<T> extends ResultSetSlice<T> {

	default List<T> list(int maxResults, int firstResult) {
		return iterator(maxResults, firstResult).list();
	}

	Cursor<T> iterator(int maxResults, int firstResult);

	default Iterable<PageResponse<T>> forEachPage(int page, int size) {
		return list(PageRequest.of(page, size));
	}

}
