package com.github.paganini2008.devtools.jdbc;

import java.util.List;

import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * 
 * ResultSetSlice
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-04
 * @version 1.0
 */
public interface ResultSetSlice<T> extends Countable {

	List<T> list(int maxResults, int firstResult);

	default PageResponse<T> list(PageRequest pageRequest) {
		return new SimplePageResponse<T>(pageRequest, this);
	}

	default List<T> list() {
		return list(-1);
	}

	default List<T> list(int maxResults) {
		return list(maxResults, 0);
	}

	default T first() {
		return ListUtils.getFirst(list(1));
	}
}
