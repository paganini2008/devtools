/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.sql.Connection;
import java.sql.SQLException;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * PageableQueryImpl
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class PageableQueryImpl extends PageableResultSetSlice<Tuple> implements PageableQuery<Tuple> {

	private final ConnectionFactory connectionFactory;
	private final PageableSql pageableSql;
	private final PreparedStatementCallback callback;

	public PageableQueryImpl(ConnectionFactory connectionFactory, PageableSql pageableSql, PreparedStatementCallback callback) {
		this.connectionFactory = connectionFactory;
		this.pageableSql = pageableSql;
		this.callback = callback;
	}

	public int rowCount() {
		final String sql = pageableSql.countableSql();
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			return JdbcUtils.fetchOne(connection, sql, callback, Integer.class);
		} catch (SQLException e) {
			throw new PageableException(e.getMessage(), e);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	public Cursor<Tuple> cursor(int maxResults, int firstResult) {
		final String execution = pageableSql.pageableSql(maxResults, firstResult);
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			return JdbcUtils.cursor(connection, execution, callback);
		} catch (SQLException e) {
			throw new PageableException(e.getMessage(), e);
		}
	}

}
