package com.github.paganini2008.springworld.webcrawler.jdbc;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EnhancedJdbcTemplateResultSetSlice
 *
 * @author Fred Feng
 * @created 2019-08
 * @version 1.0
 */
@Slf4j
public class EnhancedJdbcTemplateResultSetSlice<T> implements ResultSetSlice<T> {

	private final String sql;
	private final SqlParameterSource sqlParameterSource;
	private final RowMapper<T> rowMapper;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public EnhancedJdbcTemplateResultSetSlice(String sql, SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper,
			NamedParameterJdbcTemplate jdbcTemplate) {
		this.sql = sql;
		this.sqlParameterSource = sqlParameterSource;
		this.rowMapper = rowMapper;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int totalCount() {
		final String sql = "select count(1) from (" + this.sql + ") as this";
		if (log.isTraceEnabled()) {
			log.trace("Execute Sql: " + sql);
		}
		return jdbcTemplate.queryForObject(sql, sqlParameterSource, Integer.class);
	}

	@Override
	public List<T> list(int maxResults, int firstResult) {
		final String sql = getPageableSql(maxResults, firstResult);
		if (log.isTraceEnabled()) {
			log.trace("Execute Sql: " + sql);
		}
		return jdbcTemplate.query(sql, sqlParameterSource, rowMapper);
	}

	protected String getPageableSql(int maxResults, int firstResult) {
		if (maxResults < 0) {
			return this.sql;
		}
		return this.sql + " limit " + firstResult + "," + maxResults;
	}

}
