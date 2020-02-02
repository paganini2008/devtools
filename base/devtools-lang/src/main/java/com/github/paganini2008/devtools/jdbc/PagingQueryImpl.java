package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * SqlQueryImpl
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public class PagingQueryImpl implements PagingQuery<Tuple> {

	private final Connection connection;
	private final PageableSql pageableSql;
	private final PreparedStatementCallback callback;

	public PagingQueryImpl(Connection connection, PageableSql pageableSql, PreparedStatementCallback callback) {
		this.connection = connection;
		this.pageableSql = pageableSql;
		this.callback = callback;
	}

	public int totalCount() {
		final String sql = pageableSql.countableSql();
		try {
			Object result = DBUtils.executeOneResultQuery(connection, sql);
			return result instanceof Number ? ((Number) result).intValue() : 0;
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

	public Iterator<Tuple> iterator(int maxResults, int firstResult) {
		final String execution = pageableSql.pageableSql(maxResults, firstResult);
		try {
			return DBUtils.executeQuery(connection, execution, callback);
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

}
