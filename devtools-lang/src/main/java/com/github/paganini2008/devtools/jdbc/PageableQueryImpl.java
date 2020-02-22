package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * PageableQueryImpl
 *
 * @author Fred Feng
 * @version 1.0
 */
public class PageableQueryImpl implements PageableQuery<Tuple> {

	private final ConnectionFactory connectionFactory;
	private final PageableSql pageableSql;
	private final PreparedStatementCallback callback;

	public PageableQueryImpl(ConnectionFactory connectionFactory, PageableSql pageableSql, PreparedStatementCallback callback) {
		this.connectionFactory = connectionFactory;
		this.pageableSql = pageableSql;
		this.callback = callback;
	}

	public int totalCount() {
		final String sql = pageableSql.countableSql();
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			return JdbcUtils.fetchOne(connection, sql, Integer.class);
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
