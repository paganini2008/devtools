package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * PageableQuery
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface PageableQuery<T> extends ResultSetSlice<T> {

	default List<T> list(int maxResults, int firstResult) {
		List<T> results = new ArrayList<T>();
		Cursor<T> cursor = cursor(maxResults, firstResult);
		for (T t : CollectionUtils.forEach(cursor)) {
			results.add(t);
		}
		return results;
	}

	Cursor<T> cursor(int maxResults, int firstResult);

	default Iterable<PageResponse<T>> forEach(int page, int size) {
		return list(PageRequest.of(page, size));
	}

}
