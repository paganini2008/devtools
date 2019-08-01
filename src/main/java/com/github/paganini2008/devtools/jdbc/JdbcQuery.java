package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * JdbcQuery
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public interface JdbcQuery<T> extends ResultSetSlice<T> {

	default List<T> list(int maxResults, int firstResult) {
		List<T> list = new ArrayList<T>();
		for (T tuple : CollectionUtils.forEach(iterator(maxResults, firstResult))) {
			list.add(tuple);
		}
		return list;
	}

	Iterator<T> iterator(int maxResults, int firstResult);

}
