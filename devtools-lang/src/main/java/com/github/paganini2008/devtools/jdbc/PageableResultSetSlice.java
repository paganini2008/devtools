package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * PageableResultSetSlice
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class PageableResultSetSlice<T> implements ResultSetSlice<T> {

	protected int pageNumber;

	@Override
	public PageResponse<T> list(PageRequest pageRequest) {
		PageResponse<T> pageResponse = new SimplePageResponse<T>(pageRequest, this);
		this.pageNumber = pageResponse.getPageNumber();
		return pageResponse;
	}

	public int getPageNumber() {
		return pageNumber;
	}

}
