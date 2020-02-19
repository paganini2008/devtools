package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * PageableQuery
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface PageableQuery<T> extends ResultSetSlice<T> {

	default List<T> list(int maxResults, int firstResult) {
		List<T> list = new ArrayList<T>();
		Iterator<T> iterator = iterator(maxResults, firstResult);
		for (T tuple : CollectionUtils.forEach(iterator)) {
			list.add(tuple);
		}
		return list;
	}

	Iterator<T> iterator(int maxResults, int firstResult);

	default Iterable<PageResponse<T>> forEachPage(int page, int size) {
		return list(PageRequest.of(page, size));
	}

}
