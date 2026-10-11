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
 * DefaultPageableSql builds count and {@code limit/offset} paging statements
 * for databases supporting that syntax (MySQL, PostgreSQL, H2 etc.).
 *
 * @author Fred Feng
 * @date 2020-02-02
 * @since 2.0.1
 */
public class DefaultPageableSql implements PageableSql {

	public DefaultPageableSql(String sql) {
		this.sql = sql;
	}

	private final String sql;

	/**
	 * Returns {@code select count(1) as rowCount from (sql) as this}.
	 */
	@Override
	public String countableSql() {
		return new StringBuilder(sql).insert(0, "select count(1) as rowCount from (").append(") as this").toString();
	}

	/**
	 * Appends {@code limit maxResults offset firstResult}; nothing is appended
	 * if maxResults is not positive, and the offset is omitted if negative.
	 */
	@Override
	public String pageableSql(int maxResults, int firstResult) {
		boolean hasLimit = false;
		StringBuilder copy = new StringBuilder(sql);
		if (maxResults > 0) {
			copy.append(" limit ").append(maxResults);
			hasLimit = true;
		}
		if (firstResult >= 0 && hasLimit) {
			copy.append(" offset ").append(firstResult);
		}
		return copy.toString();
	}

	protected String getSql() {
		return sql;
	}

}