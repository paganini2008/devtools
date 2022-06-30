/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.jdbc;

import java.io.Serializable;

/**
 * 
 * SimplePageRequest
 *
 * @author Fred Feng
 *
 * @since 2.0.1
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
