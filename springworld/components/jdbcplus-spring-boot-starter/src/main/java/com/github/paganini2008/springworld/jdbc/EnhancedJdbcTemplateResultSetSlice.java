package com.github.paganini2008.springworld.jdbc;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EnhancedJdbcTemplateResultSetSlice
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2020-01
 * @version 1.0
 */
@Slf4j
public class EnhancedJdbcTemplateResultSetSlice<T> implements ResultSetSlice<T> {

	private final SqlParameterSource sqlParameterSource;
	private final RowMapper<T> rowMapper;
	private final PageableSql pageableSql;
	private final NamedParameterJdbcOperations jdbcOperations;

	public EnhancedJdbcTemplateResultSetSlice(NamedParameterJdbcOperations jdbcOperations, PageableSql pageableSql,
			SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		this.jdbcOperations = jdbcOperations;
		this.pageableSql = pageableSql;
		this.sqlParameterSource = sqlParameterSource;
		this.rowMapper = rowMapper;
	}

	@Override
	public int totalCount() {
		final String execution = pageableSql.countableSql();
		if (log.isTraceEnabled()) {
			log.trace("Execute Sql: " + execution);
		}
		return jdbcOperations.queryForObject(execution, sqlParameterSource, Integer.class);
	}

	@Override
	public List<T> list(int maxResults, int firstResult) {
		final String execution = pageableSql.pageableSql(maxResults, firstResult);
		if (log.isTraceEnabled()) {
			log.trace("Execute Sql: " + execution);
		}
		return jdbcOperations.query(execution, sqlParameterSource, rowMapper);
	}

	public NamedParameterJdbcOperations getJdbcOperations() {
		return jdbcOperations;
	}

}
