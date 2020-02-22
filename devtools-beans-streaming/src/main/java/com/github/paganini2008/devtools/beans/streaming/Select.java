package com.github.paganini2008.devtools.beans.streaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Select
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Select<E> implements Selectable<E> {

	private List<E> content;

	Select(List<E> content) {
		this.content = content;
	}

	public static <E> Select<E> from(Collection<E> content) {
		return new Select<E>(content instanceof List ? (List<E>) content : new ArrayList<E>(content));
	}

	public Selectable<E> filter(Predicate<E> predicate) {
		content = content.stream().filter(predicate).collect(Collectors.toList());
		return this;
	}

	public Selectable<E> distinct() {
		content = content.stream().distinct().collect(Collectors.toList());
		return this;
	}

	public Selectable<E> orderBy(Sort<E> sort) {
		content.sort(sort);
		return this;
	}

	public <T> Groupable<E> groupBy(Function<E, T> function, String attributeName) {
		return new Groups<E>(content).groupBy(function, attributeName);
	}

	public List<E> list(int maxResults, int firstResult) {
		return ListUtils.slice(content, maxResults, firstResult);
	}

	public int totalCount() {
		return content.size();
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer) {
		return new MemoryResultSetSlice<E, T>(content, transformer);
	}

}
