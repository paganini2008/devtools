package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.DBUtils.PreparedStatementCallback;

/**
 * 
 * SqlQueryImpl
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public class SqlQueryImpl implements SqlQuery<Tuple> {

	private final Connection connection;
	private final String sql;
	private final PreparedStatementCallback callback;

	public SqlQueryImpl(Connection connection, String sql, PreparedStatementCallback callback) {
		this.connection = connection;
		this.sql = sql;
		this.callback = callback;
	}

	public int totalCount() {
		final String sql = "select count(1) from (" + this.sql + ")";
		try {
			Object result = DBUtils.executeOneResultQuery(connection, sql);
			return result instanceof Number ? ((Number) result).intValue() : 0;
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

	public Iterator<Tuple> iterator(int maxResults, int firstResult) {
		final String pageableSql = getPageableSql(sql, maxResults, firstResult);
		try {
			return DBUtils.executeQuery(connection, pageableSql, callback);
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

	public String getSql() {
		return sql;
	}

	protected String getPageableSql(String sql, int maxResults, int firstResult) {
		return sql + " limit " + maxResults + "," + firstResult;
	}

}
