package com.allyes.components.jpahelper.support.hibernate;

import javax.persistence.EntityManager;

import com.allyes.components.jpahelper.support.EntityDao;
import com.allyes.components.jpahelper.support.EntityDaoSupport;
import com.allyes.components.jpahelper.support.ResultSetSlice;
import com.allyes.components.jpahelper.support.RowMapper;

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
