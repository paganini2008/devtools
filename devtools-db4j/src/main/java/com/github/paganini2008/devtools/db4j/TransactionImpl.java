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
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.jdbc.PageableQuery;
import com.github.paganini2008.devtools.jdbc.PageableSql;

/**
 * 
 * TranscationImpl
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class TransactionImpl implements Transaction {

	private final Connection connection;
	private final ParsedSqlRunner sqlRunner;
	private final AtomicBoolean completed;

	TransactionImpl(Connection connection, ParsedSqlRunner sqlRunner) {
		this.connection = connection;
		this.sqlRunner = sqlRunner;
		this.completed = new AtomicBoolean(false);
	}

	@Override
	public int update(String sql, SqlParameter sqlParameter) throws SQLException {
		return sqlRunner.update(connection, sql, sqlParameter);
	}

	@Override
	public int update(String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException {
		return sqlRunner.update(connection, sql, sqlParameter, generatedKey);
	}

	@Override
	public int[] batchUpdate(String sql, SqlParameters sqlParameters) throws SQLException {
		return sqlRunner.batchUpdate(connection, sql, sqlParameters);
	}

	@Override
	public <T> T query(String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException {
		return sqlRunner.query(connection, sql, sqlParameter, extractor);
	}

	@Override
	public List<Tuple> queryForList(String sql, SqlParameter sqlParameter) throws SQLException {
		return sqlRunner.queryForList(connection, sql, sqlParameter);
	}

	@Override
	public <T> T queryForObject(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		return sqlRunner.queryForObject(connection, sql, sqlParameter, rowMapper);
	}

	@Override
	public <T> List<T> queryForList(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		return sqlRunner.queryForList(connection, sql, sqlParameter, rowMapper);
	}

	@Override
	public <T> Cursor<T> queryForCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		return sqlRunner.queryForCursor(connection, sql, sqlParameter, rowMapper);
	}

	@Override
	public <T> Cursor<T> queryForCachedCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		return sqlRunner.queryForCachedCursor(connection, sql, sqlParameter, rowMapper);
	}

	@Override
	public PageableQuery<Tuple> queryForPage(PageableSql pageableSql, SqlParameter sqlParameter) throws SQLException {
		return sqlRunner.queryForPage(() -> connection, pageableSql, sqlParameter);
	}

	@Override
	public <T> PageableQuery<T> queryForPage(PageableSql pageableSql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		return sqlRunner.queryForPage(() -> connection, pageableSql, sqlParameter, rowMapper);
	}

	public <T> T customize(Customizable<T> customizable) throws SQLException {
		return customizable.customize(connection, sqlRunner);
	}

	@Override
	public void rollback() {
		try {
			JdbcUtils.rollback(connection);
			completed.set(true);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}

	}

	@Override
	public void commit() {
		try {
			JdbcUtils.commit(connection);
			completed.set(true);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	@Override
	public void close() {
		JdbcUtils.closeQuietly(connection);
	}

	@Override
	public boolean isCompleted() {
		return completed.get();
	}

}
