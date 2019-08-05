package com.allyes.components.jpahelper.support;

import java.util.List;

import com.allyes.developer.utils.collection.ListUtils;

/**
 * 
 * ResultSetSlice
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-04
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
