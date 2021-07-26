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
package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.PageableQuery;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.PooledConnectionFactory;
import com.github.paganini2008.devtools.jdbc.UnpooledConnectionFactory;
import com.github.paganini2008.devtools.jdbc.UnpooledDataSource;

/**
 * 
 * SqlPlus
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class SqlPlus implements JdbcOperations {

	private final ConnectionFactory connectionFactory;
	private final ParsedSqlRunner sqlRunner;

	public SqlPlus(String driverClassName, String jdbcUrl, String user, String password, int poolSize) {
		this(new UnpooledDataSource(driverClassName, jdbcUrl, user, password, poolSize));
	}

	public SqlPlus(String driverClassName, String jdbcUrl, String user, String password) {
		this(new UnpooledConnectionFactory(driverClassName, jdbcUrl, user, password));
	}

	public SqlPlus(DataSource dataSource) {
		this(new PooledConnectionFactory(dataSource));
	}

	public SqlPlus(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		this.sqlRunner = new ParsedSqlRunner();
	}

	@Override
	public int update(String sql, SqlParameter sqlParameter) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.update(connection, sql, sqlParameter);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public int update(String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.update(connection, sql, sqlParameter, generatedKey);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public int[] batchUpdate(String sql, SqlParameters sqlParameters) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.batchUpdate(connection, sql, sqlParameters);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public <T> T query(String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.query(connection, sql, sqlParameter, extractor);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public List<Tuple> queryForList(String sql, SqlParameter sqlParameter) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForList(connection, sql, sqlParameter);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public <T> T queryForObject(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForObject(connection, sql, sqlParameter, rowMapper);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public <T> List<T> queryForList(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForList(connection, sql, sqlParameter, rowMapper);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public <T> Cursor<T> queryForCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForCursor(connection, sql, sqlParameter, rowMapper);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public <T> Cursor<T> queryForCachedCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForCachedCursor(connection, sql, sqlParameter, rowMapper);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public PageableQuery<Tuple> queryForPage(PageableSql pageableSql, SqlParameter sqlParameter) throws SQLException {
		return sqlRunner.queryForPage(connectionFactory, pageableSql, sqlParameter);
	}

	@Override
	public <T> PageableQuery<T> queryForPage(PageableSql pageableSql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		return sqlRunner.queryForPage(connectionFactory, pageableSql, sqlParameter, rowMapper);
	}

	public <T> T customize(Customizable<T> customizable) throws SQLException {
		Connection connection = getConnection();
		try {
			return customizable.customize(connection, sqlRunner);
		} finally {
			connectionFactory.close(connection);
		}
	}

	protected Connection getConnection() throws SQLException {
		return connectionFactory.getConnection();
	}

	public Transaction beginTransaction() throws SQLException {
		return beginTransaction(connection -> {
		});
	}

	public Transaction beginTransaction(ConnectionSettings connectionSettings) throws SQLException {
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		if (connectionSettings != null) {
			connectionSettings.applySettings(connection);
		}
		return new TransactionImpl(connection, sqlRunner);
	}

	public ParsedSqlRunner getSqlRunner() {
		return sqlRunner;
	}

}
