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

import java.util.concurrent.Executor;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * JdbcDumpOptions configures how {@link JdbcDumpTemplate} writes the rows to
 * the destination.
 *
 * @author Fred Feng
 * @date 2020-02-02
 *
 * @since 2.0.2
 */
public interface JdbcDumpOptions {

	/**
	 * Returns the destination catalog, null to keep the default one.
	 */
	default String getCatalog() {
		return null;
	}

	/**
	 * Returns the destination schema, null to keep the default one.
	 */
	default String getSchema() {
		return null;
	}

	/**
	 * Returns the executor writing the rows, null to write in the caller thread.
	 * The dump waits for all writes to finish either way.
	 */
	default Executor getExecutor() {
		return null;
	}

	/**
	 * Returns the max count of rows to read, non-positive meaning no limit.
	 */
	default long getMaxRecords() {
		return -1L;
	}

	/**
	 * Returns the insert statement of the row. When dumping tables, a blank
	 * value means a statement built from the table name and the column labels.
	 */
	String getInsertionSql(Tuple t);

	/**
	 * Returns the parameters of the insert statement, the row values by default.
	 */
	default Object[] getArgs(Tuple t) {
		return t != null ? t.toValues() : new Object[0];
	}

	/**
	 * Returns the filter of the rows to write, accepting all rows by default.
	 */
	default Predicate<Tuple> getPredicate() {
		return t -> true;
	}

}
