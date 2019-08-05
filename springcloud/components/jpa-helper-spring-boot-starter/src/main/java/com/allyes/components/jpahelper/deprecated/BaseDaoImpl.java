package com.allyes.components.jpahelper.deprecated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.data.jpa.domain.Specification;

import com.allyes.components.jpahelper.support.hibernate.HibernateDaoSupport;
import com.allyes.developer.utils.collection.ListUtils;

/**
 * 
 * BaseDaoImpl
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Deprecated
@SuppressWarnings("all")
public class BaseDaoImpl<E, ID> extends HibernateDaoSupport<E, ID> implements BaseDao<E, ID> {

	private final EntityManager em;
	private final Class<E> domainClass;

	public BaseDaoImpl(Class<E> domainClass, EntityManager em) {
		super(domainClass, em);
		this.em = em;
		this.domainClass = domainClass;
	}

	public Class<E> getDomainClass() {
		return domainClass;
	}

	public Object getSingleResult(String sql, Object[] arguments) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		Optional<?> result = query.uniqueResultOptional();
		return result.isPresent() ? result.get() : null;
	}

	public List<Map<String, ?>> queryForMap(String sql, Object[] arguments) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (List<Map<String, ?>>) query.getResultList();
	}

	public List<Map<String, ?>> queryForMap(String sql, Object[] arguments, int offset, int limit) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (List<Map<String, ?>>) query.getResultList();
	}

	public List<E> query(String sql, Object[] arguments) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.addEntity(domainClass);
		return (List<E>) query.getResultList();
	}

	public List<E> query(String sql, Object[] arguments, int offset, int limit) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		query.addEntity(domainClass);
		return (List<E>) query.getResultList();
	}

	public <T> List<T> query(String sql, Object[] arguments, Class<T> resultClass) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, ?>> results = (List<Map<String, ?>>) query.getResultList();
		return BeanCopyUtils.copyFrom(results, resultClass);
	}

	public <T> List<T> query(String sql, Object[] arguments, int offset, int limit, Class<T> resultClass) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, ?>> results = (List<Map<String, ?>>) query.getResultList();
		return BeanCopyUtils.copyFrom(results, resultClass);
	}

	public E queryOne(String sql, Object[] arguments) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.setMaxResults(1);
		query.addEntity(domainClass);
		List<E> results = (List<E>) query.getResultList();
		return ListUtils.getFirst(results);
	}

	public <T> T queryOne(String sql, Object[] arguments, Class<T> resultClass) {
		Session session = em.unwrap(Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql);
		if (arguments != null && arguments.length > 0) {
			int index = 1;
			for (Object arg : arguments) {
				query.setParameter(index++, arg);
			}
		}
		query.setMaxResults(1);
		query.addEntity(domainClass);
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(resultClass));
		List<T> results = (List<T>) query.getResultList();
		return ListUtils.getFirst(results);
	}

}
