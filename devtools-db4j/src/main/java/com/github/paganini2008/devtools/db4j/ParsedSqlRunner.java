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
import com.github.paganini2008.devtools.jdbc.PageableResultSetSlice;
import com.github.paganini2008.devtools.jdbc.PageableSql;

/**
 * 
 * ParsedSqlRunner
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ParsedSqlRunner {

	private final SqlRunner sqlRunner;

	public ParsedSqlRunner() {
		this.sqlRunner = new SqlRunner();
	}

	public <T> T query(Connection connection, String sql, Object[] args, ResultSetExtractor<T> extractor) throws SQLException {
		return query(connection, sql, new ArraySqlParameter(args), extractor);
	}

	public <T> T query(Connection connection, String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.query(connection, rawSql, parameters, jdbcTypes, extractor);
	}

	public List<Tuple> queryForList(Connection connection, String sql, Object[] args) throws SQLException {
		return queryForList(connection, sql, new ArraySqlParameter(args));
	}

	public List<Tuple> queryForList(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForList(connection, rawSql, parameters, jdbcTypes);
	}

	public Cursor<Tuple> queryForCursor(Connection connection, String sql, Object[] args) throws SQLException {
		return queryForCursor(connection, sql, new ArraySqlParameter(args));
	}

	public Cursor<Tuple> queryForCursor(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForCursor(connection, rawSql, parameters, jdbcTypes);
	}

	public Cursor<Tuple> queryForCursor(Connection connection, String sql) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		return sqlRunner.queryForCursor(connection, rawSql);
	}

	public <T> List<T> queryForList(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForList(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> Cursor<T> queryForCursor(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForCursor(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> Cursor<T> queryForCachedCursor(Connection connection, String sql, Object[] args, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForCachedCursor(connection, sql, new ArraySqlParameter(args), rowMapper);
	}

	public <T> Cursor<T> queryForCachedCursor(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForCachedCursor(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public Cursor<Tuple> queryForCachedCursor(Connection connection, String sql, Object[] args) throws SQLException {
		return queryForCachedCursor(connection, sql, new ArraySqlParameter(args));
	}

	public Cursor<Tuple> queryForCachedCursor(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForCachedCursor(connection, rawSql, parameters, jdbcTypes);
	}

	public Cursor<Tuple> queryForCachedCursor(Connection connection, String sql) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		return sqlRunner.queryForCachedCursor(connection, rawSql);
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
		return queryForPage(connectionFactory, pageableSql, sqlParameter, new TupleRowMapper());
	}

	public <T> T queryForObject(Connection connection, String sql, SqlParameter sqlParameter, Class<T> requiredType) throws SQLException {
		return queryForObject(connection, sql, sqlParameter, new ColumnIndexRowMapper<T>(requiredType));
	}

	public <T> T queryForObject(Connection connection, String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.queryForObject(connection, rawSql, parameters, jdbcTypes, rowMapper);
	}

	public int[] batchUpdate(Connection connection, String sql, SqlParameters sqlParameters) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		List<Object[]> parameterList = sqlParameters != null ? getArgumentsList(parsedSql, sqlParameters) : null;
		JdbcType[] jdbcTypes = sqlParameters != null ? getJdbcTypes(parsedSql, sqlParameters) : null;
		return sqlRunner.batchUpdate(connection, rawSql, parameterList, jdbcTypes);
	}

	public int update(Connection connection, String sql, SqlParameter sqlParameter) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.update(connection, rawSql, parameters, jdbcTypes);
	}

	public int update(Connection connection, String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String rawSql = parsedSql.toString();
		Object[] parameters = sqlParameter != null ? getArguments(parsedSql, sqlParameter) : null;
		JdbcType[] jdbcTypes = sqlParameter != null ? getJdbcTypes(parsedSql, sqlParameter) : null;
		return sqlRunner.update(connection, rawSql, parameters, jdbcTypes, generatedKey);
	}

	private List<Object[]> getArgumentsList(ParsedSql parsedSql, SqlParameters sqlParameters) {
		String[] parameterNames = parsedSql.getParameterNames();
		String[] defaultValues = parsedSql.getDefaultValues();
		List<Object[]> results = new ArrayList<Object[]>();
		for (int index = 0; index < sqlParameters.getSize(); index++) {
			Object[] arguments = new Object[parameterNames.length];
			for (int i = 0; i < parameterNames.length; i++) {
				arguments[i] = sqlParameters.hasValue(index, parameterNames[i]) ? sqlParameters.getValue(index, parameterNames[i])
						: defaultValues[i];
			}
			results.add(arguments);
		}
		return results;
	}

	private Object[] getArguments(ParsedSql parsedSql, SqlParameter sqlParameter) {
		String[] parameterNames = parsedSql.getParameterNames();
		String[] defaultValues = parsedSql.getDefaultValues();
		Object[] arguments = new Object[parameterNames.length];
		for (int i = 0; i < parameterNames.length; i++) {
			arguments[i] = sqlParameter.hasValue(parameterNames[i]) ? sqlParameter.getValue(parameterNames[i]) : defaultValues[i];
		}
		return arguments;
	}

	private JdbcType[] getJdbcTypes(ParsedSql parsedSql, SqlType sqlType) {
		String[] parameterNames = parsedSql.getParameterNames();
		JdbcType[] results = new JdbcType[parameterNames.length];
		int i = 0;
		for (String parameterName : parameterNames) {
			results[i++] = sqlType.getJdbcType(parameterName);
		}
		return results;
	}

	protected ParsedSql getParsedSql(String sql) {
		return ParsedSql.parse(sql);
	}

	public SqlRunner getSqlRunner() {
		return sqlRunner;
	}

	/**
	 * 
	 * PageableQueryImpl
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class PageableQueryImpl<T> extends PageableResultSetSlice<T> implements PageableQuery<T> {

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
		public int rowCount() {
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
		public Cursor<T> cursor(int maxResults, int firstResult) {
			final String sql = pageableSql.pageableSql(maxResults, firstResult);
			Connection connection = null;
			try {
				connection = connectionFactory.getConnection();
				if (SqlRunner.useCachedRowSet) {
					return sqlRunner.queryForCachedCursor(connection, sql, sqlParameter, rowMapper);
				}
				return sqlRunner.queryForCursor(connection, sql, sqlParameter, rowMapper);
			} catch (SQLException e) {
				throw new PageableException(e.getMessage(), e);
			}
		}

	}

}
