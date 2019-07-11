package com.github.paganini2008.devtools.beans.streaming;

import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * MemoryResultSetSlice
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @version 1.0
 */
public class MemoryResultSetSlice<E, T> implements ResultSetSlice<T> {

	private final List<E> content;
	private final Transformer<E, T> transformer;

	MemoryResultSetSlice(List<E> content, Transformer<E, T> transformer) {
		this.content = content;
		this.transformer = transformer;
	}

	public List<T> list(int maxResults, int firstResult) {
		List<T> results = new ArrayList<T>();
		List<E> subList = maxResults > firstResult ? content.subList(firstResult, maxResults) : content;
		for (E element : subList) {
			T data = transformer.transfer(element);
			results.add(data);
		}
		return results;
	}

	public int totalCount() {
		return content.size();
	}

}
