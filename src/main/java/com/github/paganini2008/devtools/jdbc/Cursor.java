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
package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * Cursor iterates the rows of a query lazily. The underlying resources are
 * released when the rows are exhausted or {@link #close()} is called.
 *
 * @author Fred Feng
 * @date 2020-02-21
 * @since 2.0.1
 */
public interface Cursor<T> extends Iterator<T> {

	/**
	 * Reads all remaining rows into a list; the cursor is exhausted (and closed)
	 * afterwards.
	 * 
	 * @return the remaining rows
	 */
	default List<T> list() {
		List<T> results = new ArrayList<T>();
		for (T t : CollectionUtils.forEach(this)) {
			results.add(t);
		}
		return results;
	}
	
	/**
	 * Returns the current row number, starting from 1.
	 * 
	 * @throws DetachedSqlException if the cursor has been closed
	 */
	int getRownum();
	
	/**
	 * Moves to the given row so that the next row read is {@code rownum + 1}.
	 * 
	 * @param rownum the absolute row number
	 */
	void mark(int rownum);

	/**
	 * Returns false once the rows are exhausted or the cursor is closed.
	 */
	boolean isOpened();

	/**
	 * Releases the underlying resources. Calling it more than once has no effect.
	 */
	void close();

}
