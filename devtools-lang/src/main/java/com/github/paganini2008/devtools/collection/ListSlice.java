package com.github.paganini2008.devtools.collection;

import java.util.Collection;
import java.util.List;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * ListSlice
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class ListSlice<T> implements ResultSetSlice<T> {

	private final List<T> list;

	public ListSlice(List<T> list) {
		this.list = list;
	}

	@Override
	public int rowCount() {
		return list.size();
	}

	@Override
	public List<T> list(int maxResults, int firstResult) {
		return ListUtils.slice(list, maxResults, firstResult);
	}

	public static <T> ListSlice<T> wrap(Collection<T> c) {
		return new ListSlice<T>(ListUtils.toList(c));
	}

}
