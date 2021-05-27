package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * ResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ResultSetSlice<T> extends Listable<T>, Countable {

	default PageResponse<T> list(PageRequest pageRequest) {
		return new SimplePageResponse<T>(pageRequest, this);
	}

	default PageResponse<T> list(PageRequest pageRequest, int maxResults) {
		return new SimplePageResponse<T>(pageRequest, this, () -> maxResults);
	}

	default PageResponse<T> list(PageRequest pageRequest, Countable countable) {
		return new SimplePageResponse<T>(pageRequest, this, countable);
	}
}
