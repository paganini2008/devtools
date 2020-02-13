package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * PagingQueryImpl
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public class PagingQueryImpl implements PagingQuery<Tuple> {

	private final ConnectionFactory connectionFactory;
	private final PageableSql pageableSql;
	private final PreparedStatementSetter callback;

	public PagingQueryImpl(ConnectionFactory connectionFactory, PageableSql pageableSql, PreparedStatementSetter callback) {
		this.connectionFactory = connectionFactory;
		this.pageableSql = pageableSql;
		this.callback = callback;
	}

	public int totalCount() {
		final String sql = pageableSql.countableSql();
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			Object result = DBUtils.executeOneResultQuery(connection, sql);
			return result instanceof Number ? ((Number) result).intValue() : 0;
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

	public Iterator<Tuple> iterator(int maxResults, int firstResult) {
		final String execution = pageableSql.pageableSql(maxResults, firstResult);
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			return DBUtils.executeQuery(connection, execution, callback);
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

}
