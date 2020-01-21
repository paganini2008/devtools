package com.github.paganini2008.springworld.webcrawler.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.lang.Nullable;

/**
 * 
 * EnhancedNamedParameterJdbcDaoSupport
 *
 * @author Fred Feng
 * @created 2019-08
 * @version 1.0
 */
public class EnhancedNamedParameterJdbcDaoSupport extends NamedParameterJdbcDaoSupport {

	@Nullable
	private EnhancedNamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected void initTemplateConfig() {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		if (jdbcTemplate != null) {
			this.jdbcTemplate = new EnhancedNamedParameterJdbcTemplate(jdbcTemplate);
		}
	}

	@Override
	public EnhancedNamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return this.jdbcTemplate;
	}

}
