package com.github.paganini2008.springworld.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.lang.Nullable;

/**
 * 
 * EnhancedJdbcDaoSupport
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2020-01
 * @version 1.0
 */
public class EnhancedJdbcDaoSupport extends NamedParameterJdbcDaoSupport {

	@Nullable
	private EnhancedJdbcTemplate jdbcTemplate;

	@Override
	protected void initTemplateConfig() {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		if (jdbcTemplate != null) {
			this.jdbcTemplate = new EnhancedJdbcTemplate(jdbcTemplate);
		}
	}

	@Override
	public EnhancedJdbcTemplate getNamedParameterJdbcTemplate() {
		return this.jdbcTemplate;
	}

}
