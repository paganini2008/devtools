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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Selector
 * <p>
 * Entry of an in-memory query, the default {@link Selectable} implementation.
 * See {@link #from(java.util.Collection)}.
 *
 * @author Fred Feng
 * @date 2019-07-11
 * @since 2.0.1
 */
public class Selector<E> implements Selectable<E> {

	private List<E> content;

	Selector(List<E> content) {
		this.content = content;
	}

	/**
	 * Create a selector on a snapshot of the given collection. The source
	 * collection is never modified by any subsequent operation (filter, sort,
	 * etc.)
	 * 
	 * @param content the source collection
	 * @return a new selector
	 */
	public static <E> Selector<E> from(Collection<E> content) {
		Assert.isNull(content, "Source collection must not be null.");
		return new Selector<E>(new ArrayList<E>(content));
	}

	public Selectable<E> filter(Predicate<E> predicate) {
		content = content.stream().filter(predicate).collect(Collectors.toList());
		return this;
	}

	public Selectable<E> distinct() {
		content = content.stream().distinct().collect(Collectors.toList());
		return this;
	}

	public Selectable<E> orderBy(Sorter<E> sort) {
		Assert.isNull(sort, "Sorter must not be null.");
		content.sort(sort);
		return this;
	}

	public <T> Groupable<E> groupBy(Function<E, T> function, String attributeName) {
		return new Grouper<E>(content).groupBy(function, attributeName);
	}

	/**
	 * Returns a page of the elements, like sql 'limit maxResults offset firstResult'. A negative
	 * maxResults means no limit, an offset beyond the end results in an empty list.
	 * @throws IllegalArgumentException if firstResult is negative
	 */
	public List<E> list(int maxResults, int firstResult) {
		return MemoryResultSetSlice.slice(content, maxResults, firstResult);
	}

	public int rowCount() {
		return content.size();
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer) {
		return new MemoryResultSetSlice<E, T>(content, transformer);
	}

}
