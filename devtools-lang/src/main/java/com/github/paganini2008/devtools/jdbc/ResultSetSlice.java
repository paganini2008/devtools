package com.github.paganini2008.devtools.jdbc;

import java.util.List;

import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * 
 * ResultSetSlice
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface ResultSetSlice<T> extends Countable {

	List<T> list(int maxResults, int firstResult);

	default PageResponse<T> list(PageRequest pageRequest) {
		return new SimplePageResponse<T>(pageRequest, this);
	}

	default PageResponse<T> list(PageRequest pageRequest, int maxResults) {
		return new SimplePageResponse<T>(pageRequest, this, () -> maxResults);
	}

	default PageResponse<T> list(PageRequest pageRequest, Countable countable) {
		return new SimplePageResponse<T>(pageRequest, this, countable);
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
