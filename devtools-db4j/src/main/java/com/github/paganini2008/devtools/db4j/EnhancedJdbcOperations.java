package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.ColumnIndexRowMapper;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * 
 * EnhancedJdbcOperations
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class EnhancedJdbcOperations extends JdbcOperations {

	private static final int sqlCacheSize = 256;
	private final LruMap<String, ParsedSql> parsedSqlCache = new LruMap<String, ParsedSql>(sqlCacheSize);
	private TokenParser tokenParser = new DefaultTokenParser("?");

	public void setTokenParser(TokenParser tokenParser) {
		this.tokenParser = tokenParser;
	}

	public <T> T query(Connection connection, String sql, ParameterSource parameterSource, ResultSetExtractor<T> extractor)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return query(connection, originalSql, parameters, jdbcTypes, extractor);
	}

	public <T> List<Tuple> queryForList(Connection connection, String sql, ParameterSource parameterSource)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return queryForList(connection, originalSql, parameters, jdbcTypes);
	}

	public <T> Iterator<Tuple> iterator(Connection connection, String sql, ParameterSource parameterSource)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return iterator(connection, originalSql, parameters, jdbcTypes);
	}

	public <T> List<T> queryForList(Connection connection, String sql, ParameterSource parameterSource, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return queryForList(connection, originalSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, ParameterSource parameterSource, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return iterator(connection, originalSql, parameters, jdbcTypes, rowMapper);
	}

	public <T> T queryForObject(Connection connection, String sql, ParameterSource parameterSource, Class<T> requiredType)
			throws SQLException {
		return queryForObject(connection, sql, parameterSource,
				new ColumnIndexRowMapper<T>(getTypeHandlerRegistry(), requiredType));
	}

	public <T> T queryForObject(Connection connection, String sql, ParameterSource parameterSource, RowMapper<T> rowMapper)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return queryForObject(connection, originalSql, parameters, jdbcTypes, rowMapper);
	}

	public int[] batch(Connection connection, String sql, ParameterListSource listSource) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		List<Object[]> parameterList = listSource != null ? getParameterList(parsedSql, listSource) : null;
		JdbcType[] jdbcTypes = listSource != null ? getJdbcTypes(parsedSql, listSource) : null;
		return batch(connection, originalSql, parameterList, jdbcTypes);
	}

	public int update(Connection connection, String sql, ParameterSource parameterSource) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return update(connection, originalSql, parameters, jdbcTypes);
	}

	public int update(Connection connection, String sql, ParameterSource parameterSource, KeyHolder keyHolder) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String originalSql = parsedSql.toString();
		Object[] parameters = parameterSource != null ? getParameters(parsedSql, parameterSource) : null;
		JdbcType[] jdbcTypes = parameterSource != null ? getJdbcTypes(parsedSql, parameterSource) : null;
		return update(connection, originalSql, parameters, jdbcTypes, keyHolder);
	}

	private List<Object[]> getParameterList(ParsedSql parsedSql, ParameterListSource listSource) {
		List<Object[]> results = new ArrayList<Object[]>();
		for (int index = 0; index < listSource.getSize(); index++) {
			List<Object> list = new ArrayList<Object>();
			for (String paramName : parsedSql.getParameterNames()) {
				list.add(listSource.hasValue(index, paramName) ? listSource.getValue(index, paramName) : null);
			}
			results.add(list.toArray());
		}
		return results;
	}

	private Object[] getParameters(ParsedSql parsedSql, ParameterSource parameterSource) {
		Object[] results = new Object[parsedSql.getParameterNames().size()];
		int i = 0;
		for (String paramName : parsedSql.getParameterNames()) {
			results[i++] = parameterSource.hasValue(paramName) ? parameterSource.getValue(paramName) : null;
		}
		return results;
	}

	private JdbcType[] getJdbcTypes(ParsedSql parsedSql, ParameterSource parameterSource) {
		JdbcType[] results = new JdbcType[parsedSql.getParameterNames().size()];
		int i = 0;
		for (String paramName : parsedSql.getParameterNames()) {
			results[i++] = parameterSource.getJdbcType(paramName);
		}
		return results;
	}

	private JdbcType[] getJdbcTypes(ParsedSql parsedSql, ParameterListSource listSource) {
		JdbcType[] results = new JdbcType[parsedSql.getParameterNames().size()];
		int i = 0;
		for (String paramName : parsedSql.getParameterNames()) {
			results[i++] = listSource.getJdbcType(paramName);
		}
		return results;
	}

	private ParsedSql getParsedSql(String sql) {
		ParsedSql parsedSql = parsedSqlCache.get(sql);
		if (parsedSql == null) {
			parsedSqlCache.put(sql, tokenParser.parse(sql));
			parsedSql = parsedSqlCache.get(sql);
		}
		return parsedSql;
	}

}
