package com.allyes.components.jpahelper.support;

import java.io.Serializable;

/**
 * 
 * SimplePageRequest
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class SimplePageRequest implements PageRequest, Serializable {

	private static final long serialVersionUID = 3775599342290915269L;

	SimplePageRequest(int page, int size) {
		if (page < 1) {
			throw new IllegalArgumentException("Page index must be greater than zero!");
		}
		if (size < 1) {
			throw new IllegalArgumentException("Page size must be greater than zero!");
		}
		this.page = page;
		this.size = size;
	}

	private int page;
	private final int size;

	public int getPageNumber() {
		return page;
	}

	public int getPageSize() {
		return size;
	}

	public int getOffset() {
		return (page - 1) * size;
	}

	public PageRequest next() {
		return new SimplePageRequest(getPageNumber() + 1, getPageSize());
	}

	public PageRequest previous() {
		return getPageNumber() == 1 ? this : new SimplePageRequest(getPageNumber() - 1, getPageSize());
	}

	public PageRequest first() {
		return new SimplePageRequest(1, getPageSize());
	}

	public PageRequest set(int page) {
		return new SimplePageRequest(page, getPageSize());
	}

	public String toString() {
		return "Page: " + getPageNumber() + ", Size: " + getPageSize();
	}

}
