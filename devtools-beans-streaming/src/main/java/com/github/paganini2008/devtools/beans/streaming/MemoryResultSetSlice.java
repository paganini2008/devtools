/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.List;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * MemoryResultSetSlice
 * 
 * @author Fred Feng
 * 
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
		List<E> subList = ListUtils.slice(content, maxResults, firstResult);
		for (E element : subList) {
			T data = transformer.transfer(element);
			results.add(data);
		}
		return results;
	}

	public int rowCount() {
		return content.size();
	}

}
