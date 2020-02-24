package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;

/**
 * 
 * TranscationImpl
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TransactionImpl implements Transaction {

	private final Connection connection;
	private final ParsedSqlRunner sqlRunner;

	TransactionImpl(Connection connection, ParsedSqlRunner sqlRunner) {
		this.connection = connection;
		this.sqlRunner = sqlRunner;
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

	public <T> T customize(Customizable<T> customizable) throws SQLException {
		return customizable.customize(connection, sqlRunner);
	}

	@Override
	public void rollback() {
		JdbcUtils.rollbackQuietly(connection);
	}

	@Override
	public void commit() {
		JdbcUtils.commitAndCloseQuietly(connection);
	}

}
