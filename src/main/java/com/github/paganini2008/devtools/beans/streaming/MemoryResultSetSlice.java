/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.beans.streaming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * MemoryResultSetSlice
 * <p>
 * A {@link com.github.paganini2008.devtools.jdbc.ResultSetSlice} over an in-
 * memory list, transforming each element on paging.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
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
		List<E> subList = slice(content, maxResults, firstResult);
		for (E element : subList) {
			T data = transformer.transfer(element);
			results.add(data);
		}
		return results;
	}

	public int rowCount() {
		return content.size();
	}

	/**
	 * Paging like sql 'limit maxResults offset firstResult'. A negative
	 * maxResults means no limit, an offset beyond the size results in an empty
	 * list. The returned list is an unmodifiable copy.
	 */
	static <T> List<T> slice(List<T> list, int maxResults, int firstResult) {
		if (firstResult < 0) {
			throw new IllegalArgumentException("firstResult must not be negative: " + firstResult);
		}
		final int size = list.size();
		final int from = Math.min(firstResult, size);
		final int to = maxResults < 0 ? size : (int) Math.min((long) from + maxResults, size);
		return Collections.unmodifiableList(new ArrayList<T>(list.subList(from, to)));
	}

}
