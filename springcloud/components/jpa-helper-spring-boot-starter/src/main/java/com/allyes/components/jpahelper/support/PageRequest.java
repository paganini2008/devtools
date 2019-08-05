package com.allyes.components.jpahelper.support;

/**
 * 
 * PageRequest
 * 
 * @author Fred Feng
 * @created 2019-04
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
