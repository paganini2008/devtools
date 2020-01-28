package com.github.paganini2008.springworld.webcrawler.jdbc;

import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * EnhancedNamedParameterJdbcTemplate
 *
 * @author Fred Feng
 * @created 2019-08
 * @version 1.0
 */
public class EnhancedNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {

	public EnhancedNamedParameterJdbcTemplate(JdbcOperations jdbcOperations) {
		super(jdbcOperations);
	}

	public ResultSetSlice<Map<String, Object>> slice(String sql, SqlParameterSource sqlParameterSource) {
		return slice(sql, sqlParameterSource, new ColumnMapRowMapper());
	}

	public <T> ResultSetSlice<T> slice(String sql, SqlParameterSource sqlParameterSource, Class<T> resultClass) {
		return slice(sql, sqlParameterSource, new BeanPropertyRowMapper<T>(resultClass));
	}

	public <T> ResultSetSlice<T> slice(String sql, SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
		return new EnhancedJdbcTemplateResultSetSlice<T>(sql, sqlParameterSource, rowMapper, this);
	}

}
