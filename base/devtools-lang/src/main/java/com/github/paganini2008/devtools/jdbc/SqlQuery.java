package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SqlQuery
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public interface SqlQuery<T> extends ResultSetSlice<T> {

	default List<T> list(int maxResults, int firstResult) {
		List<T> list = new ArrayList<T>();
		Iterator<T> iterator = iterator(maxResults, firstResult);
		for (T tuple : CollectionUtils.forEach(iterator)) {
			list.add(tuple);
		}
		return list;
	}

	Iterator<T> iterator(int maxResults, int firstResult);

	String getSql();

	default Iterable<PageResponse<T>> forEach(int page, int size) {
		return list(PageRequest.of(page, size));
	}

}
