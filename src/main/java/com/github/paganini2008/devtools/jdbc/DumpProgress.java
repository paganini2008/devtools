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

/**
 * 
 * DumpProgress receives progress notifications from {@link JdbcDumpTemplate}.
 *
 * @author Fred Feng
 * @date 2020-02-02
 *
 * @since 2.0.2
 */
public interface DumpProgress {

	/**
	 * Called before a query or table is dumped.
	 */
	default void onStart(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
	}

	/**
	 * Called after rows are written.
	 * 
	 * @param progressRecords the count of rows written so far
	 * @param totalRecords the count of rows of the query
	 */
	void progress(String catalog, String schema, String sql, Object[] args, long progressRecords, long totalRecords,
			JdbcDumpOptions dumpOptions);

	/**
	 * Called after a query or table has been dumped and all writes finished.
	 */
	default void onEnd(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
	}

	static final DumpProgress DEFAULT = new DumpProgress() {

		@Override
		public void progress(String catalog, String schema, String sql, Object[] args, long progressRecords, long totalRecords,
				JdbcDumpOptions dumpOptions) {
		}
	};
}
