package com.github.paganini2008.springworld.webcrawler.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.webcrawler.utils.Resource;

/**
 * 
 * JdbcResourceService
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Component
public class JdbcResourceService extends NamedParameterJdbcDaoSupport implements ResourceService {

	private static final String SQL_INSERT = "insert into crawler_resource (title,content,path,create_date) values (?,?,?,?)";

	@Autowired
	public void setSuperDataSource(final DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	public int saveResource(Resource resource) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(resource);
		return getNamedParameterJdbcTemplate().update(SQL_INSERT, paramSource);
	}
}
