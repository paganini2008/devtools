package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.jdbc.PageableQuery;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.PooledConnectionFactory;
import com.github.paganini2008.devtools.jdbc.TransactionIsolationLevel;
import com.github.paganini2008.devtools.jdbc.UnpooledConnectionFactory;
import com.github.paganini2008.devtools.jdbc.UnpooledDataSource;

/**
 * 
 * SqlPlus
 *
 * @author Fred Feng
 * @version 1.0
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

	private TransactionIsolationLevel transactionIsolationLevel;

	public void setTransactionIsolationLevel(TransactionIsolationLevel transactionIsolationLevel) {
		this.transactionIsolationLevel = transactionIsolationLevel;
	}

	@Override
	public int update(String sql, SqlParameter sqlParameter) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.update(connection, sql, sqlParameter);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public int update(String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.update(connection, sql, sqlParameter, generatedKey);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public int[] batchUpdate(String sql, SqlParameters sqlParameters) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.batchUpdate(connection, sql, sqlParameters);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public <T> T query(String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.query(connection, sql, sqlParameter, extractor);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public List<Tuple> queryForList(String sql, SqlParameter sqlParameter) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForList(connection, sql, sqlParameter);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public <T> T queryForObject(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForObject(connection, sql, sqlParameter, rowMapper);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public <T> List<T> queryForList(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForList(connection, sql, sqlParameter, rowMapper);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public <T> Cursor<T> queryForCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForCursor(connection, sql, sqlParameter, rowMapper);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	@Override
	public <T> Cursor<T> queryForCachedCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = getConnection();
		try {
			return sqlRunner.queryForCachedCursor(connection, sql, sqlParameter, rowMapper);
		} finally {
			JdbcUtils.closeQuietly(connection);
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
			JdbcUtils.closeQuietly(connection);
		}
	}

	protected Connection getConnection() throws SQLException {
		return connectionFactory.getConnection();
	}

	public Transaction beginTransaction() throws SQLException {
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		if (transactionIsolationLevel != null) {
			connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
		}
		return new TransactionImpl(connection, sqlRunner);
	}

}