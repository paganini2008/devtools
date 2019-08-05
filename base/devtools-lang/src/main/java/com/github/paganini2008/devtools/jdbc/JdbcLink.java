package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * JdbcLink
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public class JdbcLink implements JdbcQuery<Tuple> {

	private final DataSource ds;
	private final String sql;
	private final List<Object> arguments = new ArrayList<Object>();

	public JdbcLink(DataSource ds, String sql, Object... arguments) {
		this.ds = ds;
		this.sql = sql;
		if (arguments != null) {
			this.arguments.addAll(Arrays.asList(arguments));
		}
	}

	public int totalCount() {
		final String sql = "select count(1) from (" + this.sql + ")";
		try {
			Object result = DBUtils.executeOneResultQuery(ds.getConnection(), sql);
			return result instanceof Number ? ((Number) result).intValue() : 0;
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

	public Iterator<Tuple> iterator(int maxResults, int firstResult) {
		final String sql = getPageableSql(maxResults, firstResult);
		arguments.add(maxResults);
		arguments.add(firstResult);
		try {
			return DBUtils.executeQuery(ds.getConnection(), sql, arguments.toArray());
		} catch (SQLException e) {
			throw new JdbcException(e.getMessage(), e);
		}
	}

	protected String getPageableSql(int maxResults, int firstResult) {
		return this.sql + " limit ?,?";
	}

}
