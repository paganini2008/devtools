package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * DefaultPageableSql
 *
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultPageableSql implements PageableSql {

	public DefaultPageableSql(String sql) {
		this.sql = sql;
	}

	private final String sql;

	@Override
	public String countableSql() {
		return new StringBuilder(sql).insert(0, "select count(1) as rowCount from (").append(") as this").toString();
	}

	@Override
	public String pageableSql(int maxResults, int firstResult) {
		boolean hasLimit = false;
		StringBuilder copy = new StringBuilder(sql);
		if (maxResults > 0) {
			copy.append(" limit ").append(maxResults);
			hasLimit = true;
		}
		if (firstResult >= 0 && hasLimit) {
			copy.append(" offset ").append(firstResult);
		}
		return copy.toString();
	}

}