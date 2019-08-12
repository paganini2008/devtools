package com.github.paganini2008.springworld.fastjpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * NativeQueryResultSetSlice
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-04
 * @version 1.0
 */
public class NativeQueryResultSetSlice<E> implements ResultSetSlice<E> {

	private final String sql;
	private final Object[] arguments;
	private final EntityManager em;
	private final Class<E> entityClass;

	NativeQueryResultSetSlice(String sql, Object[] arguments, EntityManager em, Class<E> entityClass) {
		this.sql = sql;
		this.arguments = arguments;
		this.em = em;
		this.entityClass = entityClass;
	}

	public int totalCount() {
		Query query = em.createNativeQuery(getCountableSql(sql), Integer.class);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		Object result = query.getSingleResult();
		return result instanceof Number ? ((Number) result).intValue() : 0;
	}

	protected String getCountableSql(String sql) {
		return new StringBuilder("select count(1) as rowCount from (").append(sql).append(")").toString();
	}

	@SuppressWarnings("unchecked")
	public List<E> list(int maxResults, int firstResult) {
		Query query = em.createNativeQuery(sql, entityClass);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		if (firstResult >= 0) {
			query.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}

}
