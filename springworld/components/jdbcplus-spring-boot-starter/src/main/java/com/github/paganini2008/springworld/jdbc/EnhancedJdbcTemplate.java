package com.github.paganini2008.springworld.jdbc;

import java.util.Map;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

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

	/**
	 * 
	 * DefaultPageableSql
	 *
	 * @author Fred Feng
	 * @created 2019-10
	 * @revised 2020-01
	 * @version 1.0
	 */
	public static class DefaultPageableSql implements PageableSql {

		public DefaultPageableSql(String sql) {
			this.sql = new StringBuilder(sql);
		}

		private final StringBuilder sql;

		@Override
		public String countableSql() {
			return sql.insert(0, "select count(1) as rowCount from (").append(") as this").toString();
		}

		@Override
		public String pageableSql(int maxResults, int firstResult) {
			if (maxResults > 0) {
				sql.append(" limit ").append(maxResults);
			}
			if (firstResult > 0) {
				sql.append(",").append(firstResult);
			}
			return sql.toString();
		}

	}

}
