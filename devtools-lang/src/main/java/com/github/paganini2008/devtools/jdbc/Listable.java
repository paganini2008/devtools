package com.github.paganini2008.devtools.jdbc;

import java.util.List;

import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * 
 * Listable
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface Listable<T> {

	default List<T> list() {
		return list(-1);
	}

	default List<T> list(int maxResults) {
		return list(maxResults, 0);
	}

	default T first() {
		return ListUtils.getFirst(list(1));
	}

	List<T> list(int maxResults, int firstResult);

}
