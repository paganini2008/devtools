package com.github.paganini2008.springworld.jdbc;

import java.util.Map;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.jdbc.DefaultPageableSql;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * EnhancedNamedParameterJdbcTemplate
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2020-01
 * @version 1.0
 */
@Component
public class EnhancedJdbcTemplate extends NamedParameterJdbcTemplate {

	public EnhancedJdbcTemplate(JdbcOperations jdbcOperations) {
		super(jdbcOperations);
	}

	public ResultSetSlice<Map<String, Object>> slice(String sql, SqlParameterSource sqlParameterSource) {
		return slice(sql, sqlParameterSource, new ColumnMapRowMapper());
	}

	public <T> ResultSetSlice<T> slice(String sql, SqlParameterSource sqlParameterSource, Class<T> elementClass) {
		return slice(sql, sqlParameterSource, new SingleColumnRowMapper<T>(elementClass));
	}

	public <T> ResultSetSlice<T> slice(String sql, SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		return slice(new DefaultPageableSql(sql), sqlParameterSource, rowMapper);
	}

	public <T> ResultSetSlice<Map<String, Object>> slice(PageableSql pageableSql, SqlParameterSource sqlParameterSource) {
		return new EnhancedJdbcTemplateResultSetSlice<Map<String, Object>>(this, pageableSql, sqlParameterSource, new ColumnMapRowMapper());
	}

	public <T> ResultSetSlice<T> slice(PageableSql pageableSql, SqlParameterSource sqlParameterSource, Class<T> elementClass) {
		return new EnhancedJdbcTemplateResultSetSlice<T>(this, pageableSql, sqlParameterSource, new SingleColumnRowMapper<T>(elementClass));
	}

	public <T> ResultSetSlice<T> slice(PageableSql pageableSql, SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		return new EnhancedJdbcTemplateResultSetSlice<T>(this, pageableSql, sqlParameterSource, rowMapper);
	}

}
