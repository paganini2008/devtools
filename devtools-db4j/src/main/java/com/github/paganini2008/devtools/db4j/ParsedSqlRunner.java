package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.ColumnIndexRowMapper;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.db4j.mapper.TupleRowMapper;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.DefaultPageableSql;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.jdbc.PageableException;
import com.github.paganini2008.devtools.jdbc.PageableQuery;
import com.github.paganini2008.devtools.jdbc.PageableSql;

/**
 * 
 * ParsedSqlRunner
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ParsedSqlRunner extends SqlRunner {

	public <T> T query(Connection connection, String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return query(connection, rawSql, parameters, jdbcTypes, extractor);
	}

	public <T> List<Tuple> queryForList(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return queryForList(connection, rawSql, parameters, jdbcTypes);
	}

	public <T> Cursor<Tuple> iterator(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return iterator(connection, rawSql, parameters, jdbcTypes);
	}

	public <T> List<T> queryForList(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return queryForList(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return iterator(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> Cursor<T> cachedIterator(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return cachedIterator(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> PageableQuery<T> queryForPage(ConnectionFactory connectionFactory, String sql, SqlParameter sqlParameter,
			RowMapper<T> rowMapper) {
		return new PageableQueryImpl<T>(connectionFactory, new DefaultPageableSql(sql), sqlParameter, rowMapper, this);
	}

	public <T> PageableQuery<T> queryForPage(ConnectionFactory connectionFactory, PageableSql pageableSql, SqlParameter sqlParameter,
			RowMapper<T> rowMapper) {
		return new PageableQueryImpl<T>(connectionFactory, pageableSql, sqlParameter, rowMapper, this);
	}

	public PageableQuery<Tuple> queryForPage(ConnectionFactory connectionFactory, String sql, SqlParameter sqlParameter) {
		return queryForPage(connectionFactory, new DefaultPageableSql(sql), sqlParameter);
	}

	public PageableQuery<Tuple> queryForPage(ConnectionFactory connectionFactory, PageableSql pageableSql, SqlParameter sqlParameter) {
		return queryForPage(connectionFactory, pageableSql, sqlParameter, new TupleRowMapper(getTypeHandlerRegistry()));
	}

	public <T> T queryForObject(Connection connection, String sql, SqlParameter sqlParameter, Class<T> requiredType) throws SQLException {
		return queryForObject(connection, sql, sqlParameter, new ColumnIndexRowMapper<T>(getTypeHandlerRegistry(), requiredType));
	}

	public <T> T queryForObject(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return queryForObject(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public int[] batch(Connection connection, String sql, SqlParameters sqlParameterSet) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		List<Object[]> parameterList = sqlParameterSet != null ? getParameterList(parsedSql, sqlParameterSet) : null;
		JdbcType[] jdbcTypes = sqlParameterSet != null ? getJdbcTypes(parsedSql, sqlParameterSet) : null;
		return batch(connection, rawSql, parameterList, jdbcTypes);
	}

	public int update(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return update(connection, rawSql, parameters, jdbcTypes);
	}

	public int update(Connection connection, String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getParameters(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return update(connection, rawSql, parameters, jdbcTypes, generatedKey);
	}

	private List<Object[]> getParameterList(ParsedSql parsedSql, SqlParameters sqlParameterSet) {
		List<Object[]> results = new ArrayList<Object[]>();
		for (int index = 0; index < sqlParameterSet.getSize(); index++) {
			List<Object> list = new ArrayList<Object>();
			for (String paramName : parsedSql.getParameterNames()) {
				list.add(sqlParameterSet.hasValue(index, paramName) ? sqlParameterSet.getValue(index, paramName) : null);
			}
			results.add(list.toArray());
		}
		return results;
	}

	private Object[] getParameters(ParsedSql parsedSql, SqlParameter sqlParameter) {
		Object[] results = new Object[parsedSql.getParameterNames().size()];
		int i = 0;
		for (String paramName : parsedSql.getParameterNames()) {
			results[i++] = sqlParameter.hasValue(paramName) ? sqlParameter.getValue(paramName) : null;
		}
		return results;
	}

	private JdbcType[] getJdbcTypes(ParsedSql parsedSql, SqlParameter sqlParameter) {
		JdbcType[] results = new JdbcType[parsedSql.getParameterNames().size()];
		int i = 0;
		for (String paramName : parsedSql.getParameterNames()) {
			results[i++] = sqlParameter.getJdbcType(paramName);
		}
		return results;
	}

	private JdbcType[] getJdbcTypes(ParsedSql parsedSql, SqlParameters sqlParameterSet) {
		JdbcType[] results = new JdbcType[parsedSql.getParameterNames().size()];
		int i = 0;
		for (String paramName : parsedSql.getParameterNames()) {
			results[i++] = sqlParameterSet.getJdbcType(paramName);
		}
		return results;
	}

	protected ParsedSql getParsedSql(String sql) {
		return ParsedSql.parse(sql);
	}

	/**
	 * 
	 * PageableQueryImpl
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class PageableQueryImpl<T> implements PageableQuery<T> {

		private final ConnectionFactory connectionFactory;
		private final PageableSql pageableSql;
		private final SqlParameter sqlParameter;
		private final RowMapper<T> rowMapper;
		private final ParsedSqlRunner sqlRunner;

		private PageableQueryImpl(ConnectionFactory connectionFactory, PageableSql pageableSql, SqlParameter sqlParameter,
				RowMapper<T> rowMapper, ParsedSqlRunner sqlRunner) {
			this.connectionFactory = connectionFactory;
			this.pageableSql = pageableSql;
			this.sqlParameter = sqlParameter;
			this.rowMapper = rowMapper;
			this.sqlRunner = sqlRunner;
		}

		@Override
		public int totalCount() {
			final String sql = pageableSql.countableSql();
			Connection connection = null;
			try {
				connection = connectionFactory.getConnection();
				return sqlRunner.queryForObject(connection, sql, sqlParameter, Integer.class);
			} catch (SQLException e) {
				throw new PageableException(e.getMessage(), e);
			} finally {
				JdbcUtils.closeQuietly(connection);
			}
		}

		@Override
		public Cursor<T> iterator(int maxResults, int firstResult) {
			final String sql = pageableSql.countableSql();
			Connection connection = null;
			try {
				connection = connectionFactory.getConnection();
				if (useCachedRowSet) {
					return sqlRunner.cachedIterator(connection, sql, sqlParameter, rowMapper);
				}
				return sqlRunner.iterator(connection, sql, sqlParameter, rowMapper);
			} catch (SQLException e) {
				throw new PageableException(e.getMessage(), e);
			}
		}

	}

}
