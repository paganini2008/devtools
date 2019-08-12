package com.github.paganini2008.springworld.fastjpa.support.hibernate;

import javax.persistence.EntityManager;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.springworld.fastjpa.support.EntityDao;
import com.github.paganini2008.springworld.fastjpa.support.EntityDaoSupport;
import com.github.paganini2008.springworld.fastjpa.support.RowMapper;

/**
 * 
 * HibernateDaoSupport
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class HibernateDaoSupport<E, ID> extends EntityDaoSupport<E, ID> implements EntityDao<E, ID> {

	public HibernateDaoSupport(Class<E> entityClass, EntityManager em) {
		super(entityClass, em);
	}

	public <T> ResultSetSlice<T> select(String sql, Object[] arguments, RowMapper<T> mapper) {
		return new HibernateRowMapperResultSetSlice<T>(sql, arguments, em, mapper);
	}

}
