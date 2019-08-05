package com.allyes.components.jpahelper.support.hibernate;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import com.allyes.components.jpahelper.support.RowMapper;
import com.allyes.components.jpahelper.support.RowMapperResultSetSlice;

/**
 * 
 * HibernateRowMapperResultSetSlice
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class HibernateRowMapperResultSetSlice<T> extends RowMapperResultSetSlice<T> {

	public HibernateRowMapperResultSetSlice(String sql, Object[] arguments, EntityManager em, RowMapper<T> mapper) {
		super(sql, arguments, em, mapper);
	}

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> queryForMap(Query query) {
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

}
