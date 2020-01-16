package com.github.paganini2008.springworld.webcrawler.dao;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.webcrawler.utils.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JdbcResourceService
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 */
@Slf4j
@Component
public class JdbcResourceService extends NamedParameterJdbcDaoSupport implements ResourceService {

	private static final String SQL_INSERTION = "insert into crawler_resources (id,title,html,url,type,create_date,version) values (:id,:title,:html,:url,:type,:createDate,:version)";

	@Autowired
	public void setSuperDataSource(final DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	public int saveResource(Resource resource) {
		resource.setId(UUID.randomUUID().toString().replace("-", ""));
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(resource);
		try {
			return getNamedParameterJdbcTemplate().update(SQL_INSERTION, paramSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}
}
