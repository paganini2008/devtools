package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * PageRequest
 * 
 * @author Jimmy Hoff
 * 
 * 
 */
public interface PageRequest {

	int getPageNumber();

	int getPageSize();

	int getOffset();

	PageRequest next();

	PageRequest previous();

	PageRequest first();

	PageRequest set(int page);

	static PageRequest of(int size) {
		return of(1, size);
	}

	static PageRequest of(int page, int size) {
		return new SimplePageRequest(page, size);
	}
}
